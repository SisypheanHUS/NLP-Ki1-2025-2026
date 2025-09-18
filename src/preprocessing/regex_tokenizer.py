from src.core.interfaces import Tokenizer
import re

class RegexTokenizer(Tokenizer):
    def tokenize(self, text: str) -> list[str]:
        # Convert to lowercase
        text = text.lower()
        # Use regex to match word characters or punctuation
        pattern = r'\w+|[^\w\s]'
        tokens = re.findall(pattern, text)
        return tokens
