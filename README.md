NLP-Ki1-2025-2026
Tổng quan
Lab 1 và Lab 2 tập trung vào xử lý ngôn ngữ tự nhiên (NLP). Lab 1 xây dựng hai công cụ tách từ (SimpleTokenizer và RegexTokenizer), thử nghiệm trên câu mẫu và tập dữ liệu UD_English-EWT. Lab 2 triển khai CountVectorizer để biểu diễn văn bản thành vector số, dùng RegexTokenizer từ Lab 1.
Công việc đã làm
Lab 1: Tách Từ

Tạo giao diện Tokenizer:

Tạo file src/core/interfaces.py với lớp trừu tượng Tokenizer, định nghĩa phương thức tokenize(text: str) -> list[str].


Xây dựng SimpleTokenizer:

Tạo file src/preprocessing/simple_tokenizer.py.
Tách từ: chuyển chữ thường, tách theo khoảng trắng, tách dấu câu (., ,, ?, !) thành token riêng. Ví dụ: "Hello, world!" → ["hello", ",", "world", "!"].


Xây dựng RegexTokenizer:

Tạo file src/preprocessing/regex_tokenizer.py.
Dùng regex (như \w+|[^\w\s]) để tách từ và dấu câu.


Tải dữ liệu:

Tạo src/core/dataset_loaders.py với hàm load_raw_text_data để đọc file CoNLL-U, lấy từ ở cột thứ hai.


Thử nghiệm:

Tạo main.py để chạy thử trên ba câu mẫu và 500 ký tự từ UD_English-EWT.



Lab 2: Count Vectorization

Cập nhật giao diện Vectorizer:

Thêm lớp Vectorizer vào src/core/interfaces.py với các phương thức: fit, transform, fit_transform.


Xây dựng CountVectorizer:

Tạo file src/representations/count_vectorizer.py.
Lớp CountVectorizer nhận RegexTokenizer, tạo từ vựng (vocabulary_) và biến văn bản thành ma trận đếm.


Thử nghiệm:

Tạo test/lab2_test.py, dùng corpus mẫu:corpus = ["I love NLP.", "I love programming.", "NLP is a subfield of AI."]


Chạy fit_transform, in từ vựng và ma trận document-term.



Kết quả chạy code
Lab 1: Câu mẫu

Câu: "Hello, world! This is a test."
SimpleTokenizer: ['hello', ',', 'world', '!', 'this', 'is', 'a', 'test', '.']
RegexTokenizer: ['hello', ',', 'world', '!', 'this', 'is', 'a', 'test', '.']


Câu: "NLP is fascinating... isn't it?"
SimpleTokenizer: ['nlp', 'is', 'fascinating', '.', '.', '.', 'isn', "'", 't', 'it', '?']
RegexTokenizer: ['nlp', 'is', 'fascinating', '.', '.', '.', 'isn', "'", 't', 'it', '?']


Câu: "Let's see how it handles 123 numbers and punctuation!"
SimpleTokenizer: ['let', "'", 's', 'see', 'how', 'it', 'handles', '123', 'numbers', 'and', 'punctuation', '!']
RegexTokenizer: ['let', "'", 's', 'see', 'how', 'it', 'handles', '123', 'numbers', 'and', 'punctuation', '!']



UD_English-EWT: Gặp lỗi do đường dẫn file sai. Đã sửa thành C:\Users\Admin\Data\UD_English-EWT\en_ewt-ud-train.txt và thêm dataset_loaders.py, nhưng chưa có kết quả cụ thể.
Lab 2: CountVectorizer
Corpus mẫu:
corpus = ["I love NLP.", "I love programming.", "NLP is a subfield of AI."]


Từ vựng:{'a': 0, 'ai': 1, 'i': 2, 'is': 3, 'love': 4, 'nlp': 5, 'of': 6, 'programming': 7, 'subfield': 8, '.': 9}


Ma trận document-term:[[0, 0, 1, 0, 1, 1, 0, 0, 0, 1],  # I love NLP.
 [0, 0, 1, 0, 1, 0, 0, 1, 0, 1],  # I love programming.
 [1, 1, 0, 1, 0, 1, 1, 0, 1, 1]]  # NLP is a subfield of AI.



Phân tích

Lab 1:

Cả hai tokenizer tách tốt câu đơn giản, chuyển chữ thường, tách dấu câu và số. Nhưng từ như "isn't" bị tách thành ['isn', "'", 't'], chưa tối ưu cho NLP.
Dấu ba chấm (...) thành ba token riêng, có thể cải thiện để xem là một token.


Lab 2:

CountVectorizer tạo từ vựng và ma trận đếm đúng, nhưng từ vựng có dấu chấm (.) do RegexTokenizer. Loại bỏ dấu câu sẽ làm từ vựng gọn hơn.
Ma trận thể hiện tần suất từ, phù hợp cho machine learning.


So sánh:

RegexTokenizer ảnh hưởng trực tiếp đến CountVectorizer. Nếu tokenizer tách "isn't" sai, từ vựng sẽ có thêm token thừa (', t), làm giảm chất lượng vector.



Cải tiến

Sửa RegexTokenizer để giữ "isn't" hoặc ... làm một token.
Loại dấu câu khỏi từ vựng trong CountVectorizer.
Thêm đối số dòng lệnh cho đường dẫn file UD_English-EWT.
