using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.DAL.Interfaces
{
    public interface IAuditable
    {
        DateTime CreationDate { get; set; }
    }
}
