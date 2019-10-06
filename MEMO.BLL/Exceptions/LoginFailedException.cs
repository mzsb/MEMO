﻿using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.BLL.Exceptions
{
    public class LoginFailedException : ExceptionBase
    {
        private const string message = "Helytelen felhasználónév vagy jelszó";

        public LoginFailedException(string message) : base(message) { }

        public LoginFailedException() : base(message) { }
    }
}
