from enum import Enum

class TokenKindType(Enum):
    """
    Class containing all supported kinds of tokens
    """
    KEYWORD = 0
    SYMBOL = 1
    IDENTIFIER = 2
    NUMBER_LITERAL = 3
    ERROR = 4
    BOOL_LITERAL = 5