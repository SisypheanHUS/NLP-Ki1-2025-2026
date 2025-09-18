from typing import List
from src.core.interfaces import Vectorizer, Tokenizer

class CountVectorizer(Vectorizer):
    def __init__(self, tokenizer: Tokenizer):
        """Khởi tạo với một tokenizer."""
        self.tokenizer = tokenizer
        self.vocabulary_ = {}  # Ánh xạ từ -> chỉ số

    def fit(self, corpus: List[str]) -> None:
        """Học từ vựng từ corpus."""
        unique_tokens = set()
        # Tách từ từ mỗi văn bản
        for doc in corpus:
            tokens = self.tokenizer.tokenize(doc)
            unique_tokens.update(tokens)
        # Gán chỉ số cho từng từ, sắp xếp để đảm bảo thứ tự nhất quán
        self.vocabulary_ = {token: idx for idx, token in enumerate(sorted(unique_tokens))}

    def transform(self, documents: List[str]) -> List[List[int]]:
        """Biến đổi văn bản thành ma trận đếm."""
        result = []
        vocab_size = len(self.vocabulary_)
        for doc in documents:
            # Tạo vector số 0
            vector = [0] * vocab_size
            # Tách từ và đếm tần suất
            tokens = self.tokenizer.tokenize(doc)
            for token in tokens:
                if token in self.vocabulary_:
                    vector[self.vocabulary_[token]] += 1
            result.append(vector)
        return result

    def fit_transform(self, corpus: List[str]) -> List[List[int]]:
        """Kết hợp fit và transform."""
        self.fit(corpus)
        return self.transform(corpus)
