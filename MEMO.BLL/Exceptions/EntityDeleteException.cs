using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.BLL.Exceptions
{
    public class EntityDeleteException : ExceptionBase
    {
        private const string message = "Az entitás törlése sikertelen";

        public EntityDeleteException() : base(message) { }

        public EntityDeleteException(string message) : base(message) { }

        public EntityDeleteException(Type type) : base($"A {type.Name} entitás törlése sikertelen") { }

        public EntityDeleteException(Type type, string message) : base($"A {type.Name} entitás törlése sikertelen: {message}") { }
    }
}
