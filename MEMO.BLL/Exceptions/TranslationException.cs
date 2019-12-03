using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.BLL.Exceptions
{
    public class TranslationException : ExceptionBase
    {
        private const string message = "A fordítás sikertelen";

        public TranslationException() : base(message) { }

        public TranslationException(string message) : base(message) { }

    }
}
