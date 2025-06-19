from .TokenType import TokenType

class Token:
    """
    Base class representing a token
    All tokens must inherit from this class.
    """
    def __init__(self, tokenType : TokenType, content : str = ""):
        """
        Sets up the token type and content

        :param tokenType: The type of the token
        :param content: The content of the token
        """
        self.type = tokenType
        self.content = content if str(tokenType) == "" else str(tokenType)

    def __str__(self):
        return self.content