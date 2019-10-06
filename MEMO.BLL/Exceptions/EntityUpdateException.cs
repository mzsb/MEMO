using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.BLL.Exceptions
{
    public class EntityUpdateException : ExceptionBase
    {
        private const string message = "Az entitás módosítása sikertelen";

        public EntityUpdateException() : base(message) { }

        public EntityUpdateException(string message) : base(message) { }

        public EntityUpdateException(Type type) : base($"A {type.Name} entitás módosítása sikertelen") { }

        public EntityUpdateException(Type type, string message) : base($"A {type.Name} entitás módosítása sikertelen: {message}") { }
    }
}
