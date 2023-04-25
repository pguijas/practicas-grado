%{
    unsigned wordCount = 0, wordCount_up = 0, wordCount_low = 0, wordCount_num = 0, phraseCount = 0, charCount = 0; 
%}

word [^ \t\n\r]+
word_min [a-záéíóúñ]+
word_may [A-ZÁÉÍÓÚÑ]+ 
digit ("+"|"-")?[0-9]+("."[0-9]+)?

%%
{digit}"."      {wordCount++; wordCount_num++; charCount += yyleng; phraseCount++;}
{digit}         {wordCount++; wordCount_num++; charCount += yyleng;}
{word_may}"."   {wordCount++; wordCount_up++;  charCount += yyleng; phraseCount++;}
{word_may}      {wordCount++; wordCount_up++;  charCount += yyleng;}
{word_min}"."   {wordCount++; wordCount_low++; charCount += yyleng; phraseCount++;}
{word_min}      {wordCount++; wordCount_low++; charCount += yyleng;}
{word}"."       {wordCount++; charCount += yyleng; phraseCount++;}
{word}          {wordCount++; charCount += yyleng;}
[ \t\n\r]       {}

%%

int main(){
    yylex();
    printf("%15s %15s %15s %15s %15s %15s\n", "wordCount", "wordCount_up", "wordCount_low", "wordCount_num", "phraseCount", "charCount");
    printf("%15d %15d %15d %15d %15d %15d\n", wordCount, wordCount_up, wordCount_low, wordCount_num, phraseCount, charCount );
    return 0;
}