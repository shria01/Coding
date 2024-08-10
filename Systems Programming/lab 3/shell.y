
/*
 * CS-252
 * shell.y: parser for shell
 *
 * This parser compiles the following grammar:
 *
 *	cmd [arg]* [> filename]
 *
 * you must extend it to understand the complete shell grammar
 *
 */

%code requires 
{

}

%union
{
  char * string;
}

%token <string> WORD PIPE
%token NOTOKEN NEWLINE STDOUT APPEND_STDOUT STDERR
%token STDOUT_STDERR STDIN BACKGROUND APPEND_STDOUT_STDERR

%{

#include <stdbool.h>
#include <stdio.h>
#include <malloc.h>
#include <fcntl.h>
#include <string.h>
#include <unistd.h>
#include "command.h"
#include "single_command.h"
#include "shell.h"

void yyerror(const char * s);
int yylex();

%}

%%

goal:
  entire_command_list
  ;

entire_command_list:
     entire_command_list entire_command {
      execute_command(g_current_command);
      g_current_command = malloc(sizeof(command_t));
      create_command(g_current_command);
     }
  |  entire_command {
      execute_command(g_current_command);
      g_current_command = malloc(sizeof(command_t));
      create_command(g_current_command);
     }
  ;

entire_command:
     single_command_list io_modifier_list NEWLINE
  |  NEWLINE
  ;

single_command_list:
     single_command_list PIPE single_command
  |  single_command
  ;

single_command:
    executable argument_list {
      insert_single_command(g_current_command, g_current_single_command);
      g_current_single_command = NULL;

    }
  ;

argument_list:
     argument_list argument
  |  /* can be empty */
  ;

argument:
     WORD {
      insert_argument(g_current_single_command, strdup(yylval.string));
     }
  ;

executable:
     WORD {
      g_current_single_command = malloc(sizeof(single_command_t));
      create_single_command(g_current_single_command);
      insert_argument(g_current_single_command, strdup(yylval.string));
     }
  ;

io_modifier_list:
     io_modifier_list io_modifier
  |  /* can be empty */
  ;

io_modifier:
     STDOUT WORD {
      g_current_command->out_file = $2;
      int fd = open(g_current_command->out_file, O_CREAT|O_TRUNC|O_RDWR,
                    0600);
      close(fd);
     }
  |  APPEND_STDOUT WORD {
      g_current_command->append_out = true;
      g_current_command->out_file = $2;
      int fd = open(g_current_command->out_file, O_CREAT|O_APPEND|O_RDWR,
                    0600);
      close(fd);
     }
  |  STDERR WORD {
      g_current_command->err_file = $2;
      int fd = open(g_current_command->out_file, O_CREAT|O_APPEND|O_RDWR,
                    0600);
      close(fd);
     }
  |  STDOUT_STDERR WORD {
      g_current_command->out_file = $2;
      g_current_command->err_file = strdup($2);
      int fd = open(g_current_command->out_file, O_CREAT|O_APPEND|O_RDWR,
                    0600);
      close(fd);
     }
  |  APPEND_STDOUT_STDERR WORD {
      g_current_command->append_out = true;
      g_current_command->append_err = true;
      g_current_command->err_file = $2;
      g_current_command->out_file = strdup($2);
      int fd = open(g_current_command->out_file, O_CREAT|O_APPEND|O_RDWR,
                    0600);
      close(fd);
     }
  |  STDIN WORD {
      g_current_command->in_file = $2;
     }
  ;

background:
     BACKGROUND
  |
  ;

%%

void
yyerror(const char * s)
{
  fprintf(stderr,"%s", s);
}

#if 0
main()
{
  yyparse();
}
#endif
