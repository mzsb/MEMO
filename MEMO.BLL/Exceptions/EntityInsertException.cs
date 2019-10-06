using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.BLL.Exceptions
{
    public class EntityInsertException : ExceptionBase
    {
        private const string message = "Az entitás beszúrása sikertelen";

        public EntityInsertException() : base(message) { }

        public EntityInsertException(string message) : base(message) { }

        public EntityInsertException(Type type) : base($"A {type.Name} entitás beszúrása sikertelen") { }

        public EntityInsertException(Type type, string message) : base($"A {type.Name} entitás beszúrása sikertelen: {message}") { }
    }
}
