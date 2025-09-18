import os
import sys

# Add the project root to sys.path to fix module imports
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))

from src.preprocessing.simple_tokenizer import SimpleTokenizer
from src.preprocessing.regex_tokenizer import RegexTokenizer
from src.core.dataset_loaders import load_raw_text_data


def main():
    # Initialize tokenizers
    simple_tokenizer = SimpleTokenizer()
    regex_tokenizer = RegexTokenizer()

    # Test sentences
    test_sentences = [
        "Hello, world! This is a test.",
        "NLP is fascinating... isn't it?",
        "Let's see how it handles 123 numbers and punctuation!"
    ]

    # Test tokenizers with sample sentences
    print("--- Testing Tokenizers with Sample Sentences ---")
    for sentence in test_sentences:
        print(f"\nOriginal: {sentence}")
        simple_tokens = simple_tokenizer.tokenize(sentence)
        regex_tokens = regex_tokenizer.tokenize(sentence)
        print(f"SimpleTokenizer Output: {simple_tokens}")
        print(f"RegexTokenizer Output: {regex_tokens}")

    # Load and test with UD_English-EWT dataset
    dataset_path = "/data/UD_English-EWT/en_ewt-ud-train.txt"
    raw_text = load_raw_text_data(dataset_path)
    sample_text = raw_text[:500]  # First 500 characters

    print("\n--- Tokenizing Sample Text from UD_English-EWT ---")
    print(f"Original Sample: {sample_text[:100]}...")
    simple_tokens = simple_tokenizer.tokenize(sample_text)
    regex_tokens = regex_tokenizer.tokenize(sample_text)
    print(f"SimpleTokenizer Output (first 20 tokens): {simple_tokens[:20]}")
    print(f"RegexTokenizer Output (first 20 tokens): {regex_tokens[:20]}")


if __name__ == "__main__":
    main()