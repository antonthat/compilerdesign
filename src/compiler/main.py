from sys import argv, exit

from lexer.Lexer import Lexer
from lexer.ErrorToken import ErrorToken

if len(argv) < 3:
    print("Invalid arguments: Expected one input file and one output file")
    exit(1)


fileName = argv[1]
if (len(argv) >= 2):
    code = ""
    with open(fileName, "r") as file:
        code = file.read()
        lexer = Lexer(code)
    print(code)
    while lexer.hasNext():
        c = lexer.nextToken()
        print(c, type(c))
        if type(c) is ErrorToken:
            exit()