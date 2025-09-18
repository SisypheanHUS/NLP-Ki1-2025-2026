def load_raw_text_data(file_path):
    """
    Load raw text from a CoNLL-U file, extracting tokens from the second column.

    Args:
        file_path (str): Path to the CoNLL-U file.

    Returns:
        str: Raw text string with tokens joined by spaces.

    Raises:
        FileNotFoundError: If the file cannot be found.
        IOError: If there's an error reading the file.
    """
    raw_text = []
    try:
        with open(file_path, 'r', encoding='utf-8') as file:
            for line in file:
                # Skip comments (lines starting with '#') and empty lines
                if line.startswith('#') or line.strip() == '':
                    continue
                # Split line by tabs (CoNLL-U format)
                fields = line.strip().split('\t')
                # Ensure the line has at least 2 columns and is not a multi-word token
                if len(fields) >= 2 and '-' not in fields[0]:
                    raw_text.append(fields[1])  # Second column contains the token
        return ' '.join(raw_text)
    except FileNotFoundError:
        raise FileNotFoundError(f"Dataset file {file_path} not found.")
    except IOError as e:
        raise IOError(f"Error reading file {file_path}: {str(e)}")