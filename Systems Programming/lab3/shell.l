
/*
 *
 * CS-252 
 * shell.l: lexical analyzer for shell
 * You have to extend it.
 *
 */

%{

#include "y.tab.h"

static  void yyunput (int c,char *buf_ptr  );

void myunputc(int c) {
  unput(c);
}

%}

%option noyywrap

%%

\n {
  return NEWLINE;
}

[ \t] {
  /* Discard spaces and tabs */
}

">" {
  return STDOUT;
}

">>" {
  return APPEND_STDOUT;
}

"2>" {
  return STDERR;
}

">&" {
  return STDOUT_STDERR;
}

">>&" {
  return APPEND_STDOUT_STDERR;
}

"<" {
  return STDIN;
}
"&" {
  return BACKGROUND;
}

"|" {
  return PIPE;
}


[^ \t\n]+ {
  /* Assume that file names have only alpha chars */
  yylval.string = strdup(yytext);
  return WORD;
}
%%
