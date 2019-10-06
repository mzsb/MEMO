using MEMO.DAL.Entities;
using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.BLL.Exceptions
{
    public class EntityNotFoundException : ExceptionBase
    {
        private const string message = "A keresett entitás nem található";

        public EntityNotFoundException() : base(message) { }

        public EntityNotFoundException(string message) : base(message) { }

        public EntityNotFoundException(Type type) : base($"A keresett {type.Name} entitás nem található") { }

        public EntityNotFoundException(Type type, string message) : base($"A keresett {type.Name} entitás nem található: {message}") { }
    }
}
