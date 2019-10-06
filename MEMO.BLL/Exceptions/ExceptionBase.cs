using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.BLL.Exceptions
{
    public abstract class ExceptionBase : Exception
    {
        public string Name { get; private set; }

        public ExceptionBase() { SetName(); }

        public ExceptionBase(string message) : base(message) { SetName(); }

        public ExceptionBase(string message, Exception innerException) : base(message, innerException) { SetName(); }

        private void SetName()
        {
            Name = GetType().Name.Replace("Exception", "");
        }
    }
}
