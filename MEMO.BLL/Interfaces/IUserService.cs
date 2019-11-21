using MEMO.DAL.Entities;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace MEMO.BLL.Interfaces
{
    public interface IUserService : IAuthorizable
    {
        Task<IEnumerable<User>> GetAsync();
        Task<User> GetByIdAsync(Guid id);
        Task UpdateAsync(User user);
        Task DeleteAsync(Guid id);
        Task<IEnumerable<User>> GetViewersByUserIdAsync(Guid id);
    }
}
