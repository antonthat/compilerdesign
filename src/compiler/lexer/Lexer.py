from enum import Enum

from .Token import Token
from .TokenType import TokenType
from .ErrorToken import ErrorToken


class Lexer:
    """
    Lexer for C0 language {https://c0.cs.cmu.edu/docs/c0-reference.pdf}
    The lexer takes the code in string format and generates a sequence of tokens within each step.
    """
    __code : str
    __currentPosition : int
    __line : int

    def __init__(self, code : str) -> None:
        """
        Sets up the lexer.
        :param code: C0 code in string format
        """
        self.__code = code
        self.__currentPosition = 0
        self.__line = 1

    def nextToken(self) -> Token:
        """
        Fetches the next token from the current position and returns the next token.
        Advances after fetching the token to the next token.
        In case of no matching token, returns ErrorToken.
        """
        error : ErrorToken = self.__skipWhiteSpace()
        if error is not None:
            return error

        if self.__currentPosition >= len(self.__code):
            return None

        defaultStepSize : int = 1
        token : Token
        match (self.__peek()):
            case "(":
                token = Token(TokenType.LEFT_PARENTHESIS)
            case ")":
                token = Token(TokenType.RIGHT_PARENTHESIS)
            case "{":
                token = Token(TokenType.LEFT_CURLY_BRACKET)
            case "}":
                token = Token(TokenType.RIGHT_CURLY_BRACKET)
            case ":":
                token = Token(TokenType.COLON)
            case ";":
                token = Token(TokenType.SEMICOLON)
            case ",":
                token = Token(TokenType.COMMA)
            case "?":
                token = Token(TokenType.TERNARY_QUESTION_MARK)
            case "~":
                token = Token(TokenType.BIT_NOT)
            case "!":
                token = Token(TokenType.BOOL_NOT, TokenType.NOT_EQUAL)
            case "&":
                token = self.handleOperator(TokenType.BIT_AND, TokenType.BIT_AND_ASSIGN, TokenType.BOOL_AND)
            case "|":
                token = self.handleOperator(TokenType.BIT_OR, TokenType.BIT_OR_ASSIGN, TokenType.BOOL_OR)
            case "^":
                token = self.handleOperator(TokenType.BIT_XOR, TokenType.BIT_XOR_ASSIGN)
            case ">":
                token = self.handleOperator(TokenType.GREATER_THAN, TokenType.GREATER_THAN_OR_EQUAL,
                                            TokenType.RIGHT_SHIFT, TokenType.RIGHT_SHIFT_ASSIGN)
            case "<":
                token = self.handleOperator(TokenType.LESS_THAN, TokenType.LESS_THAN_OR_EQUAL,
                                            TokenType.LEFT_SHIFT, TokenType.LEFT_SHIFT_ASSIGN)
            case "+":
                token = self.handleOperator(TokenType.PLUS, TokenType.PLUS_ASSIGN)
            case "-":
                token = self.handleOperator(TokenType.MINUS, TokenType.MINUS_ASSIGN)
            case "*":
                token = self.handleOperator(TokenType.TIMES, TokenType.TIMES_ASSIGN)
            case "/":
                token = self.handleOperator(TokenType.DIVIDE, TokenType.DIVIDE_ASSIGN)
            case "%":
                token = self.handleOperator(TokenType.MODULO, TokenType.MODULO_ASSIGN)
            case "=":
                token = self.handleOperator(TokenType.ASSIGN, TokenType.EQUAL)
            case _:
                if self.__isAlpha(self.__peek()) or self.__peek().isnumeric():
                    if self.__peek().isnumeric():
                        token = self.__scanNumber()
                    else:
                        token = self.__scanStringSequence()
                else:
                    token = ErrorToken(self.__line)
                defaultStepSize = 0

        self.__advance(defaultStepSize)
        return token

    def handleOperator(self, operatorTokenType: TokenType, assignOperatorTokenType: TokenType,
                       repeatingOperatorTokenType: TokenType = None,
                       repeatingAssignOperatorTokenType: TokenType = None) -> Token:
        """
        Arbitrates between assignment, repeating, repeating assignment, and generic operators

        :param operatorTokenType: generic operator (e.g., +, -, ^, &, <)
        :param assignOperatorTokenType: assignment operator (e.g., +=, -=, &=, |=, <=)
        :param repeatingOperatorTokenType: optional repeating operator (e.g., &&, >>, <<)
        :param repeatingAssignOperatorTokenType: optional repeating assignment operator (e.g., <<=, >>=)
        :return: appropriate Token
        """
        if self.hasNext():
            next_char = self.__peekNext()
            if next_char == "=":
                self.__advance()
                return Token(assignOperatorTokenType)
            elif repeatingOperatorTokenType is not None and self.hasNext() and self.__peekNext() == str(operatorTokenType):
                self.__advance()

                if repeatingAssignOperatorTokenType is not None and self.hasNext() and self.__peekNext() == "=":
                    self.__advance()
                    return Token(repeatingAssignOperatorTokenType)

                return Token(repeatingOperatorTokenType)

        return Token(operatorTokenType)

    def hasNext(self, stepSize : int = 1) -> bool:
        """
        Checks whether there is a token stepSizes away from current Token.

        :param stepSize: number of steps away from current Token
        """
        return self.__currentPosition + stepSize < len(self.__code)

    def __isHex(self, char : str) -> bool:
        """
        Checks whether char is a hexadecimal number

        :param char: Character in question
        :return: True if char is a hexadecimal number
        """
        return (char.isnumeric()
                or 'a' <= char <= 'f'
                or 'A' <= char <= 'F')

    def __isHexPrefix(self) -> bool:
        """
        Checks whether current token is a hexadecimal prefix

        :return: True if current token is a hexadecimal prefix
        """
        return (self.__peek() == '0'
                and self.hasNext()
                and (self.__peek() == 'x' or self.__peek() == 'X'))


    def __scanNumber(self) -> Token:
        """
        Scanning number for hexadecimal number or decimal number

        :return: Token containing hexadecimal number or decimal number
        """
        number : str = ""
        if self.__isHexPrefix():
            self.__advance(2)
            # nothing following 0x
            if not self.__isHex(self.__peek()):
                return ErrorToken(self.__line)

            while (self.__isHex(self.__peek())):
                number += self.__peek()
                if not self.hasNext():
                    break
                self.__advance()

            return Token(TokenType.CONST_INT, "0x" + number)

        if self.__peek() == '0' and self.hasNext() and self.__peekNext().isnumeric():
            return ErrorToken(self.__line)

        while self.__peek().isnumeric():
            number += self.__peek()
            if not self.hasNext():
                break
            self.__advance()

        return Token(TokenType.CONST_INT, number)

    def __isAlpha(self, char : str) -> bool:
        """
        Checks whether char is alphanumeric or not

        :param char: Character in question
        :return: True if char is alphanumeric or not
        """
        return ("a" <= char <= "z"
                or 'A' <= char <= 'Z'
                or char == '_')

    def __scanStringSequence(self) -> Token:
        """
        Scanning string sequence for keyword or identifier

        :return: Token containing keyword or identifier
        """
        sequence : str = ""
        while self.__isAlpha(self.__peek()) or self.__peek().isnumeric():
            sequence += self.__peek()
            self.__advance()

        for keywordTokenType in [token for token in TokenType]:
            if (str(keywordTokenType) == sequence):
                return Token(keywordTokenType)

        return Token(TokenType.IDENTIFIER, sequence)

    def __skipWhiteSpace(self) -> ErrorToken:
        """
        Skip white space
        :return:
        """
        class CommentType(Enum):
            SINGLE_LINE = 0
            MULTI_LINE = 1

        currentCommentType : CommentType = None
        multiLineCommentDepth : int = 0
        while self.hasNext(0):
            match (self.__peek()):
                case " " | "\t":
                    self.__advance()
                case "\n" | "\r":
                    self.__advance()
                    self.__line += 1
                    if currentCommentType == CommentType.SINGLE_LINE:
                        currentCommentType = None
                case "/":
                    if currentCommentType == CommentType.SINGLE_LINE:
                        self.__advance()
                        continue
                    if self.hasNext():
                        if self.__peekNext() == "/" and currentCommentType == None:
                            currentCommentType = CommentType.SINGLE_LINE
                        elif self.__peekNext() == "*":
                            currentCommentType = CommentType.MULTI_LINE
                            multiLineCommentDepth += 1
                        elif (currentCommentType == CommentType.MULTI_LINE):
                            self.__advance()
                            continue
                        else:
                            return None

                        self.__advance(2)
                        continue

                    if multiLineCommentDepth > 0:
                        self.__advance()
                        continue
                    return None
                case _:
                    if currentCommentType == CommentType.MULTI_LINE:
                        if self.__peek() == "*" and self.hasNext() and self.__peekNext() == "/":
                            self.__advance(2)
                            multiLineCommentDepth -= 1
                            currentCommentType = None if multiLineCommentDepth == 0 else CommentType.MULTI_LINE
                        else:
                            self.__advance()
                        continue
                    elif currentCommentType == CommentType.SINGLE_LINE:
                        self.__advance()
                        continue
                    return None
        if not self.hasNext(0) and currentCommentType == CommentType.MULTI_LINE:
            return ErrorToken(self.__line)
        return None

    def __peek(self) -> str:
        """
        Peeks current character in code
        :return: current character in code
        """
        return self.__code[self.__currentPosition]

    def __peekNext(self) -> str:
        """
        Peeks next character in code
        :return: next character in code
        """
        if self.hasNext():
            return self.__code[self.__currentPosition + 1]
        return ""

    def __advance(self, step : int = 1) -> None:
        """
        Advances current position by step
        :param step: steps to advance
        :return:
        """
        self.__currentPosition += step