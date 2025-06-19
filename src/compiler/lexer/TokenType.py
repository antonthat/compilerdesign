from enum import Enum
from .TokenKindType import TokenKindType

class TokenType(Enum):
    """
    Class specifying all supported tokens
    """
    BOOL_KW = "bool", TokenKindType.KEYWORD
    INT_KW = "int", TokenKindType.KEYWORD
    # redundant atm
    """
    CHAR_KW = "char", TokenKindType.KEYWORD
    STRING_KW = "string", TokenKindType.KEYWORD
    VOID_KW = "void", TokenKindType.KEYWORD
    """
    RETURN_KW = "return", TokenKindType.KEYWORD
    IF_KW = "if", TokenKindType.KEYWORD
    ELSE_KW = "else", TokenKindType.KEYWORD
    WHILE_KW = "while", TokenKindType.KEYWORD
    FOR_KW = "for", TokenKindType.KEYWORD
    BREAK_KW = "break", TokenKindType.KEYWORD
    CONTINUE_KW = "continue", TokenKindType.KEYWORD

    TRUE_KW = "true", TokenKindType.KEYWORD
    FALSE_KW = "false", TokenKindType.KEYWORD

    PRINT_KW = "print", TokenKindType.KEYWORD
    READ_KW = "read", TokenKindType.KEYWORD
    FLUSH_KW = "flush", TokenKindType.KEYWORD
    # redundant atm
    """
    NULL_KW = "null", TokenKindType.KEYWORD
    ALLOC_KW = "alloc", TokenKindType.KEYWORD
    ALLOC_ARRAY_KW = "alloc_array", TokenKindType.KEYWORD


    ASSERT_KW = "assert", TokenKindType.KEYWORD
    STRUCT_KW = "struct", TokenKindType.KEYWORD

    AUTO_KW = "auto", TokenKindType.KEYWORD
    STATIC_KW = "static", TokenKindType.KEYWORD
    EXTERN_KW = "external", TokenKindType.KEYWORD
    UNION_KW = "union", TokenKindType.KEYWORD
    CONST_KW = "const", TokenKindType.KEYWORD
    TYPEDEF_KW = "typedef", TokenKindType.KEYWORD
    SIZE_KW = "size", TokenKindType.KEYWORD
    """

    PLUS = "+", TokenKindType.SYMBOL
    MINUS = "-", TokenKindType.SYMBOL
    TIMES = "*", TokenKindType.SYMBOL
    DIVIDE = "/", TokenKindType.SYMBOL
    MODULO = "%", TokenKindType.SYMBOL
    BIT_OR = "|", TokenKindType.SYMBOL
    BIT_XOR = "^", TokenKindType.SYMBOL
    BIT_AND = "&", TokenKindType.SYMBOL
    BIT_NOT = "~", TokenKindType.SYMBOL
    LEFT_SHIFT = "<<", TokenKindType.SYMBOL
    RIGHT_SHIFT = ">>", TokenKindType.SYMBOL
    ASSIGN = "=", TokenKindType.SYMBOL
    PLUS_ASSIGN = "+=", TokenKindType.SYMBOL
    MINUS_ASSIGN = "-=", TokenKindType.SYMBOL
    TIMES_ASSIGN = "*=", TokenKindType.SYMBOL
    DIVIDE_ASSIGN = "/=", TokenKindType.SYMBOL
    MODULO_ASSIGN = "%=", TokenKindType.SYMBOL
    BIT_OR_ASSIGN = "|=", TokenKindType.SYMBOL
    BIT_AND_ASSIGN = "&=", TokenKindType.SYMBOL
    BIT_XOR_ASSIGN = "^=", TokenKindType.SYMBOL
    LEFT_SHIFT_ASSIGN = "<<=", TokenKindType.SYMBOL
    RIGHT_SHIFT_ASSIGN = ">>=", TokenKindType.SYMBOL

    # redundant atm
    """
    INCREMENT = "++", TokenKindType.SYMBOL
    DECREMENT = "--", TokenKindType.SYMBOL
    """
    EQUAL = "==", TokenKindType.SYMBOL
    NOT_EQUAL = "!=", TokenKindType.SYMBOL
    BOOL_AND = "&&", TokenKindType.SYMBOL
    BOOL_OR = "||", TokenKindType.SYMBOL
    BOOL_NOT = "!", TokenKindType.SYMBOL
    LESS_THAN = "<", TokenKindType.SYMBOL
    GREATER_THAN = ">", TokenKindType.SYMBOL
    LESS_THAN_OR_EQUAL = "<=", TokenKindType.SYMBOL
    GREATER_THAN_OR_EQUAL = ">=", TokenKindType.SYMBOL

    #redundant atm
    """
    DOUBLE_QUOTE = "\"", TokenKindType.SYMBOL
    SINGLE_QUOTE = "\'", TokenKindType.SYMBOL
    """

    LEFT_PARENTHESIS = "(", TokenKindType.SYMBOL
    RIGHT_PARENTHESIS = ")", TokenKindType.SYMBOL
    LEFT_BRACKET = "[", TokenKindType.SYMBOL
    RIGHT_BRACKET = "]", TokenKindType.SYMBOL
    LEFT_CURLY_BRACKET = "{", TokenKindType.SYMBOL
    RIGHT_CURLY_BRACKET = "}", TokenKindType.SYMBOL

    SEMICOLON = ";", TokenKindType.SYMBOL
    COLON = ":", TokenKindType.SYMBOL
    TERNARY_QUESTION_MARK = "?", TokenKindType.SYMBOL
    COMMA = ",", TokenKindType.SYMBOL
    #redundant atm
    """
    DOT = ".", TokenKindType.SYMBOL
    ARROW = "->", TokenKindType.SYMBOL
    """

    IDENTIFIER = "", TokenKindType.IDENTIFIER
    CONST_INT = "", TokenKindType.NUMBER_LITERAL

    ERROR = "error", TokenKindType.ERROR

    def __new__(cls, *args, **kwds):
        value = len(cls.__members__) + 1
        obj = object.__new__(cls)
        obj._value_ = value
        return obj

    def __init__(self, text_repr : str, kind: TokenKindType):
        self.text_repr = text_repr
        self.kind = kind

    def __str__(self):
        return self.text_repr

    def getTokenKindType(self) -> TokenKindType:
        return self.kind