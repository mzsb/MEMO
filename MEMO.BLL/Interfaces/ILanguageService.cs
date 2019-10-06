using MEMO.DAL.Entities;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace MEMO.BLL.Interfaces
{
    public interface ILanguageService
    {
        Task<IEnumerable<Language>> GetAsync();
    }
}
