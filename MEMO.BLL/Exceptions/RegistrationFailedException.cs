using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.BLL.Exceptions
{
    public class RegistrationFailedException : ExceptionBase
    {
        private const string message = "Sikertelen regisztráció";

        public RegistrationFailedException(string message) : base(message) { }

        public RegistrationFailedException() : base(message) { }
    }
}
