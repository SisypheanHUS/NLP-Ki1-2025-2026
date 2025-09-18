import os
import sys

# Thêm thư mục gốc vào sys.path
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))

from src.preprocessing.regex_tokenizer import RegexTokenizer
from src.representations.count_vectorizer import CountVectorizer

def main():
    # Khởi tạo tokenizer và vectorizer
    tokenizer = RegexTokenizer()
    vectorizer = CountVectorizer(tokenizer)

    # Corpus mẫu
    corpus = [
        "I love NLP.",
        "I love programming.",
        "NLP is a subfield of AI."
    ]

    # Chạy fit_transform
    doc_term_matrix = vectorizer.fit_transform(corpus)

    # In từ vựng và ma trận
    print("Từ vựng (vocabulary_):")
    print(vectorizer.vocabulary_)
    print("\nMa trận document-term:")
    for i, vector in enumerate(doc_term_matrix):
        print(f"Văn bản {i + 1}: {vector}")

if __name__ == "__main__":
    main()