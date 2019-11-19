using MEMO.BLL.Authorization;
using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.BLL.Interfaces
{
    public interface IAuthorizable
    {
        string Token { set; }
    }
}
