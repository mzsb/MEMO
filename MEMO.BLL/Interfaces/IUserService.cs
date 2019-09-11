using MEMO.DAL.Entities;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace MEMO.BLL.Interfaces
{
    public interface IUserService 
    {
        Task<User> LoginAsync(User user);
        Task<User> AddAsync(User user);
        Task<IEnumerable<User>> GetAsync();
        Task<User> GetByIdAsync(Guid id);
        Task UpdateAsync(User user);
        Task DeleteAsync(User user);
    }
}
