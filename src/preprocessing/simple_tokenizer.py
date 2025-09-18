from src.core.interfaces import Tokenizer
import string


class SimpleTokenizer(Tokenizer):
    def tokenize(self, text: str) -> list[str]:
        # Convert to lowercase
        text = text.lower()

        # Initialize result list
        tokens = []
        current_token = ""

        # Iterate through each character
        for char in text:
            # If character is punctuation, add current token (if any) and punctuation
            if char in string.punctuation:
                if current_token:
                    tokens.append(current_token)
                    current_token = ""
                tokens.append(char)
            # If character is whitespace, add current token (if any)
            elif char.isspace():
                if current_token:
                    tokens.append(current_token)
                    current_token = ""
            # If character is not whitespace or punctuation, add to current token
            else:
                current_token += char

        # Add final token if exists
        if current_token:
            tokens.append(current_token)

        return tokens