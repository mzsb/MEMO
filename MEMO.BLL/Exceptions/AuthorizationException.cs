using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.BLL.Exceptions
{
    public class AuthorizationException : ExceptionBase
    {
        private const string message = "Sikertelen hozzáférés az entitáshoz";

        public AuthorizationException() : base(message) { }

        public AuthorizationException(string message) : base(message) { }

        public AuthorizationException(Type type) : base($"Sikertelen hozzáférés a {type.Name} entitáshoz") { }

        public AuthorizationException(Type type, string message) : base($"Sikertelen hozzáférés a {type.Name} entitáshoz: {message}") { }
    }
}
