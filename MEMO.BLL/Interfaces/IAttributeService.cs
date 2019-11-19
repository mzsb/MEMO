using MEMO.DAL.Entities;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace MEMO.BLL.Interfaces
{
    public interface IAttributeService : IAuthorizable
    {
        Task<IEnumerable<Attribute>> GetAsync();
        Task<Attribute> GetByIdAsync(System.Guid id);
        Task<Attribute> InsertAsync(Attribute attribute);
        Task UpdateAsync(Attribute attribute);
        Task DeleteAsync(System.Guid id);

        Task<IEnumerable<Attribute>> GetByUserIdAsync(System.Guid id);
    }
}
