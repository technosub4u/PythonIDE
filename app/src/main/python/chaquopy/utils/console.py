from io import TextIOBase
from queue import Queue

class ConsoleInputStream(TextIOBase):
    """Input stream for Python console that handles both interpreter and script modes"""
    def __init__(self, task):
        self.task = task
        self.queue = Queue()
        self.buffer = ""
        self.eof = False

    @property
    def encoding(self):
        return "UTF-8"

    @property
    def errors(self):
        return "strict"

    def readable(self):
        return True

    def on_input(self, input):
        if self.eof:
            raise ValueError("Can't add more input after EOF")
        if input is None:
            self.eof = True
        self.queue.put(input)

    def read(self, size=None):
        if size is not None and size < 0:
            size = None
        buffer = self.buffer
        while (size is None or len(buffer) < size) and not self.eof:
            if self.queue.empty():
                self.task.onInputState(True)
            input = self.queue.get()
            self.task.onInputState(False)
            if input is None:  # EOF
                self.eof = True
            else:
                buffer += input

        result = buffer if size is None else buffer[:size]
        self.buffer = buffer[len(result):]
        return result

    def readline(self, size=None):
        if size is not None and size < 0:
            size = None
        chars = []
        while (size is None or len(chars) < size):
            c = self.read(1)
            if not c:
                break
            chars.append(c)
            if c == "\n":
                break
        return "".join(chars)

class ConsoleOutputStream(TextIOBase):
    """Output stream for Python console that works in both modes"""
    def __init__(self, stream, obj, method_name):
        self.stream = stream
        self.method = getattr(obj, method_name)

    def __repr__(self):
        return f"<ConsoleOutputStream {self.stream}>"

    def write(self, s):
        result = self.stream.write(s)
        s = str(s)
        self.method(s)
        return result

    def flush(self):
        self.stream.flush()

class PythonConsole:
    """Main console class that handles both interpreter and script modes"""
    def __init__(self, input_stream=None, output_stream=None, error_stream=None):
        self.input_stream = input_stream
        self.output_stream = output_stream
        self.error_stream = error_stream
        self.locals = {}
        self.history = []

    def execute(self, code, is_script_mode=False):
        """Execute Python code in either interpreter or script mode"""
        try:
            if is_script_mode:
                # Script mode: execute the entire code at once
                exec(code, self.locals, self.locals)
            else:
                # Interpreter mode: handle line by line
                for line in code.split('\n'):
                    if line.strip():
                        exec(line, self.locals, self.locals)
            return None
        except Exception as e:
            return str(e)

    def add_to_history(self, code):
        """Add executed code to history"""
        self.history.append(code)
        if len(self.history) > 10:  # Keep only last 10 entries
            self.history.pop(0)

    def get_history(self):
        """Get the command history"""
        return self.history