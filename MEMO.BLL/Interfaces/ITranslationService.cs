using MEMO.DAL.Entities;
using MEMO.DAL.Enums;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace MEMO.BLL.Interfaces
{
    public interface ITranslationService : IAuthorizable
    {
        Task<IEnumerable<Translation>> GetAsync();
        Task<Translation> GetByIdAsync(Guid id);
        Task<Translation> InsertAsync(Translation translation);
        Task UpdateAsync(Translation translation);
        Task DeleteAsync(Guid id);

        Task<IEnumerable<Translation>> GetByDictionaryIdAsync(Guid id);

        Task<Translation> TranslateAsync(string original, string from, string to);
    }
}
