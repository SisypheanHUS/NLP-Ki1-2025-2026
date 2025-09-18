from abc import ABC, abstractmethod
from typing import List

# Giao diện cho Tokenizer (từ Lab 1)
class Tokenizer(ABC):
    @abstractmethod
    def tokenize(self, text: str) -> List[str]:
        pass

# Giao diện cho Vectorizer (Lab 2)
class Vectorizer(ABC):
    @abstractmethod
    def fit(self, corpus: List[str]) -> None:
        """Học từ vựng từ danh sách văn bản."""
        pass

    @abstractmethod
    def transform(self, documents: List[str]) -> List[List[int]]:
        """Biến đổi danh sách văn bản thành ma trận đếm."""
        pass

    @abstractmethod
    def fit_transform(self, corpus: List[str]) -> List[List[int]]:
        """Kết hợp fit và transform."""
        pass
