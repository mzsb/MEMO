using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.BLL.Exceptions
{
    public abstract class ExceptionBase : Exception
    {
        public string Name { get; }

        public ExceptionBase() : base("MEMOException") { Name = GetType().Name.Replace("Exception","");}

        public ExceptionBase(string message) : base(message) { Name = GetType().Name.Replace("Exception", ""); }

        public ExceptionBase(string message, Exception innerException) : base(message, innerException) 
        {
            Name = GetType().Name.Replace("Exception", "");
        }
    }
}
