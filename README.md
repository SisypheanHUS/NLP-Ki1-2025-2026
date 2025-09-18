# NLP-Ki1-2025-2026

Lab 1: Tokenization trong NLP
Tổng quan
Lab 1 tập trung vào việc xây dựng hai công cụ tách từ (tokenizer): SimpleTokenizer và RegexTokenizer, sau đó thử nghiệm chúng trên các câu mẫu và tập dữ liệu UD_English-EWT. Mục tiêu là hiểu cách tách từ, một bước quan trọng trong xử lý ngôn ngữ tự nhiên (NLP).
Công việc đã làm

Tạo giao diện Tokenizer:

Tạo file src/core/interfaces.py với lớp trừu tượng Tokenizer, định nghĩa phương thức tokenize(text: str) -> list[str].


Xây dựng SimpleTokenizer:

Tạo file src/preprocessing/simple_tokenizer.py với lớp SimpleTokenizer.
Phương thức tokenize:
Chuyển văn bản thành chữ thường.
Tách từ theo khoảng trắng.
Tách dấu câu (như ., ,, ?, !) thành token riêng.
Ví dụ: "Hello, world!" → ["hello", ",", "world", "!"].




Xây dựng RegexTokenizer:

Tạo file src/preprocessing/regex_tokenizer.py với lớp RegexTokenizer.
Sử dụng regex (ví dụ: \w+|[^\w\s]) để tách từ và dấu câu chính xác hơn.


Xây dựng hàm tải dữ liệu:

Tạo file src/core/dataset_loaders.py với hàm load_raw_text_data để đọc file CoNLL-U (UD_English-EWT).
Hàm này lấy từ ở cột thứ hai và nối thành chuỗi văn bản.


Chạy thử nghiệm:

Tạo main.py để chạy thử hai tokenizer trên ba câu mẫu và một đoạn 500 ký tự từ UD_English-EWT.



Kết quả chạy code
Câu mẫu
Dưới đây là kết quả khi tách từ ba câu mẫu:

Câu: "Hello, world! This is a test."
SimpleTokenizer: ['hello', ',', 'world', '!', 'this', 'is', 'a', 'test', '.']
RegexTokenizer: ['hello', ',', 'world', '!', 'this', 'is', 'a', 'test', '.']


Câu: "NLP is fascinating... isn't it?"
SimpleTokenizer: ['nlp', 'is', 'fascinating', '.', '.', '.', 'isn', "'", 't', 'it', '?']
RegexTokenizer: ['nlp', 'is', 'fascinating', '.', '.', '.', 'isn', "'", 't', 'it', '?']


Câu: "Let's see how it handles 123 numbers and punctuation!"
SimpleTokenizer: ['let', "'", 's', 'see', 'how', 'it', 'handles', '123', 'numbers', 'and', 'punctuation', '!']
RegexTokenizer: ['let', "'", 's', 'see', 'how', 'it', 'handles', '123', 'numbers', 'and', 'punctuation', '!']



Dữ liệu UD_English-EWT
Phần chạy trên tập dữ liệu gặp lỗi do đường dẫn file không đúng (/data/UD_English-EWT/en_ewt-ud-train.txt). Sau khi sửa thành đường dẫn Windows (ví dụ: C:\Users\Admin\Data\UD_English-EWT\en_ewt-ud-train.txt) và thêm dataset_loaders.py, code sẽ tách từ 500 ký tự đầu và in 20 token đầu tiên. Kết quả cụ thể chưa có do lỗi file lúc chạy thử.
Phân tích kết quả

So sánh hai tokenizer:

Cả SimpleTokenizer và RegexTokenizer cho kết quả giống nhau trên các câu mẫu. Cả hai đều chuyển chữ hoa thành chữ thường, tách dấu câu và số thành token riêng.
Với từ rút gọn như "isn't" hay "Let's", cả hai tách thành ['isn', "'", 't'] và ['let', "'", 's']. Điều này đúng về mặt kỹ thuật nhưng chưa tối ưu vì trong NLP, từ như "isn't" thường được giữ nguyên.
Dấu ba chấm (...) bị tách thành ba dấu chấm riêng (., ., .). Có thể cải thiện để xem ... là một token duy nhất.


Nhận xét:

Hai tokenizer hoạt động tốt với các câu đơn giản, nhưng RegexTokenizer có tiềm năng xử lý tốt hơn các trường hợp phức tạp (như URL, emoji) nhờ regex.
Cần thử nghiệm thêm với dữ liệu đa dạng hơn để thấy sự khác biệt.



Cải tiến đề xuất

Giữ nguyên từ rút gọn như "isn't" bằng cách sửa regex của RegexTokenizer.
Xử lý dấu ba chấm (...) như một token duy nhất.
Thêm đối số dòng lệnh cho đường dẫn file dữ liệu để dễ chạy trên các máy khác.
