using MEMO.DAL.Context;
using MEMO.DAL.Entities;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace MEMO.BLL.Interfaces
{
    public interface IDictionaryService
    {
        Task<IEnumerable<Dictionary>> GetAsync();
        Task<Dictionary> GetByIdAsync(Guid id);
        Task<Dictionary> InsertAsync(Dictionary dictionary);
        Task UpdateAsync(Dictionary dictionary);
        Task DeleteAsync(Guid id);

        Task<IEnumerable<Dictionary>> GetByUserIdAsync(Guid id);
        Task<IEnumerable<Dictionary>> GetPublicAsync(Guid id);
    }
}
