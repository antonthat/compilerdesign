from .Token import Token
from .TokenType import TokenType

class ErrorToken(Token):
    """
    Error Token containing error information
    """
    line : str

    def __init__(self, line):
        Token.__init__(self, TokenType.ERROR)
        self.line = line

    def __str__(self):
        return "Parse Error in Line / Possibly an open multiline comment somewhere ages ago : " + str(self.line)