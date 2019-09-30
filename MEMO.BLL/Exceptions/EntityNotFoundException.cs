using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.BLL.Exceptions
{
    public class EntityNotFoundException : ExceptionBase
    {
        private const string message = "";

        public EntityNotFoundException() : base(message) { }
    }
}
