#include "my_malloc.h"

#include <assert.h>
#include <errno.h>
#include <math.h>
#include <pthread.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "printing.h"

/* Pointer to the location of the heap prior to any sbrk calls */
void *g_base = NULL;

/* Pointer to the end of our heap */
void *g_heap_end = NULL;

/* Pointer to the head of the free list */
header *g_freelists[NUM_LISTS] = { NULL };

/* Mutex to ensure thread safety for the freelist */
static pthread_mutex_t g_mutex = { 0 };


/*
 * Pointer to the second fencepost in the most recently allocated chunk from
 * the OS. Used for coalescing chunks
 */

header *g_last_left_fence_post = NULL;
header *g_last_right_fence_post = NULL;

/*
 * Direct the compiler to run the init function before running main
 * this allows initialization of required globals
 */

static void init(void) __attribute__((constructor));

/*
 * Insert a block at the beginning of the freelist.
 * The block is located after its left header, h.
 */

static void insert_free_block(header *h) {
  h->prev = NULL;
  h->next = g_freelists[ORDER(h)];
  if (g_freelists[ORDER(h)] != NULL) {
    g_freelists[ORDER(h)]->prev = h;
  }
  g_freelists[ORDER(h)] = h;
} /* insert_free_block() */

/*
 * Remove a block, h, from the free list it is in.
 */

static void remove_free_block(header *h) {
  if (h->prev != NULL) {
    h->prev->next = h->next;
  }
  if (h->next != NULL) {
    h->next->prev = h->prev;
  }
  if (g_freelists[ORDER(h)] == h) {
    g_freelists[ORDER(h)] = h->next;
  }
  h->next = NULL;
  h->prev = NULL;
} /* remove_free_block() */

/*
 * Instantiates fenceposts at the left and right side of a block.
 */

static void set_fenceposts(void *mem, size_t size) {
  header *left_fence = (header *) mem;
  header *right_fence = (header *) (((char *) mem) + size - ALLOC_HEADER_SIZE);
  SET_SIZE(left_fence, size - (2 * ALLOC_HEADER_SIZE));
  SET_SIZE(right_fence, ALLOC_HEADER_SIZE);
  SET_STATUS(left_fence, FENCEPOST);
  SET_STATUS(right_fence, FENCEPOST);
  left_fence->chunk = (header *) (((char *) left_fence) + ALLOC_HEADER_SIZE);
} /* set_fenceposts() */

/*
 * Constructor that runs before main() to initialize the library.
 */

__attribute__((constructor))void init() {
  /* Initialize all the free lists */
  for (int i = 0; i < NUM_LISTS; i++) {
    g_freelists[i] = NULL;
  }

  /* Initialize mutex for thread safety */

  pthread_mutex_init(&g_mutex, NULL);

  /* Manually set printf buffer so it won't call malloc */

  setvbuf(stdout, NULL, _IONBF, 0);

  /* Record the starting address of the heap */

  g_base = sbrk(0);

} /* init() */

static header *find_block(size_t size) {
  header *best_fit = g_freelists[NUM_LISTS];
  header *current_block = g_freelists[NUM_LISTS];
  while (current_block != NULL) {
    size_t curr_size = TRUE_SIZE(current_block);
    if ( curr_size >= size ) {
      if (curr_size < best_fit->size) {
        best_fit = current_block;
      }
    }
    current_block = current_block->next;
  }
  if (best_fit->size >= size) {
    return best_fit;
  }
  return NULL;
}
header* split_header(header* head, size_t needed_size) {

  /* Set the next_allocate block for the next_fit function */

  //g_next_allocate = head->next;

  /* If the size of the found_header is a perfect match or the remaining
   * memory after splitting is too small */

  if ((head->size == needed_size) ||
      ((TRUE_SIZE(head) - needed_size - ALLOC_HEADER_SIZE) <=
        ALLOC_HEADER_SIZE + sizeof(header *) * 2)) {

    /* Remove head from the Free List */

    if (head != g_freelists[NUM_LISTS]) {
      head->prev->next = head->next;
      if (head->next != NULL) {
        head->next->prev = head->prev;
      }
    }
    else {
      g_freelists[NUM_LISTS] = head->next;
      if (g_freelists[NUM_LISTS] != NULL) {
        g_freelists[NUM_LISTS]->prev = NULL;
      }
    }

    head->next = NULL;
    head->prev = NULL;

    return head;
  }
}
// Splits a block recursively until it's of the appropriate size for the request
header *split_block(header *block, size_t needed_size) {
  size_t block_size = TRUE_SIZE(block);

  // Base case: the block is the right size, or can't be split further
  if (block_size == needed_size || block_size == MIN_SIZE) {
    return block;
  }

  // Remove block from free list
  remove_free_block(block);

  // Calculate the size of the sub-blocks
  size_t half_size = block_size / 2;

  // Update the original block's size
  SET_SIZE(block, half_size);

  // Create the new split block
  header *split = (header*)((char*)block + half_size);
  SET_SIZE(split, half_size);

  // Insert both blocks into the appropriate free list
  insert_free_block(block);
  insert_free_block(split);

  // Continue the split, now looking for the appropriate sub-block
  return split_block((needed_size <= half_size) ? block : split, needed_size);
}
/*
 * TODO: implement malloc
 */

void * my_malloc(size_t size) {

  pthread_mutex_lock(&g_mutex);
  // Insert code here
  if (size == 0) {
    pthread_mutex_unlock(&g_mutex);
    return NULL;
  }
  size_t real_size = size;
  if (real_size < MIN_ALLOCATION) {
    real_size = MIN_ALLOCATION;
  }
  real_size = real_size + ALLOC_HEADER_SIZE < sizeof(header) ?
              sizeof(header) - ALLOC_HEADER_SIZE: real_size;
  size_t needed_size = requested_size + ALLOC_HEADER_SIZE;
  size_t rounded_size = 1;
  while (rounded_size < real_size) {
    rounded_size *= 2;
  }
  header *best_fit = NULL;
  size_t best_fit_size = ARENA_SIZE;
  for (int i = 0; i < NUM_LISTS; i++) {
    for (header *current = g_freelists[i]; current != NULL; current = current->next) {
      size_t block_size = TRUE_SIZE(current);
      if (block_size >= rounded_size && block_size < best_fit_size) {
        best_fit = current;
        best_fit_size = block_size;
      }
    }
  }
  if (best_fit_size == rounded_size) {
    remove_free_block(best_fit);
    SET_STATUS(best_fit, ALLOCATED);
    pthread_mutex_unlock(&g_mutex);
    return &best_fit->data;
  }
  if (best_fit_size < real_size * 2 || best_fit_size == MIN_SIZE) {
    remove_free_block(best_fit);
    SET_STATUS(best_fit, ALLOCATED);
    pthread_mutex_unlock(&g_mutex);
    return &best_fit->data;
  }
  if (best_fit_size > rounded_size * 2 ) {
    header *new_block = split_block(best_fit, rounded_size);
    SET_STATUS(new_block, ALLOCATED);
    pthread_mutex_unlock(&g_mutex);
    return &new_block->data;
  }
  pthread_mutex_unlock(&g_mutex);
} /* my_malloc() */

/*
 * TODO: implement free
 */

void my_free(void * p) {

  pthread_mutex_lock(&g_mutex);
  // Insert code here
  pthread_mutex_unlock(&g_mutex);

  // Remove this code
  (void) p;
  assert(false);
  exit(1);
} /* my_free() */

/*
 * Calls malloc and sets each byte of
 * the allocated memory to a value
 */

void *my_calloc(size_t nmemb, size_t size) {
  return memset(my_malloc(size * nmemb), 0, size * nmemb);
} /* my_calloc() */

/*
 * Reallocates an allocated block to a new size and
 * copies the contents to the new block.
 */

void *my_realloc(void *ptr, size_t size) {
  void *mem = my_malloc(size);
  memcpy(mem, ptr, size);
  my_free(ptr);
  return mem;
} /* my_realloc() */
