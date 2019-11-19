using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.BLL.Exceptions
{
    public class LoginFailedException : ExceptionBase
    {
        private const string message = "Sikertelen bejelentkezés";

        public LoginFailedException(string message) : base(message) { }

        public LoginFailedException() : base(message) { }
    }
}
