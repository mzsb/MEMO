using MEMO.BLL.Authentication;
using MEMO.BLL.Exceptions;
using MEMO.BLL.Interfaces;
using MEMO.DAL.Context;
using MEMO.DAL.Entities;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MEMO.BLL.Services
{
    public class UserService : IUserService
    {
        private readonly UserManager<User> _userManager;

        public UserService(UserManager<User> userManager)
        {
            _userManager = userManager;
        }

        public async Task<IEnumerable<User>> GetAsync()
        {
            var users = await _userManager.Users
                                          .AsNoTracking()
                                          .ToListAsync();

            foreach (var user in users)
            {
                user.Role = (await _userManager.GetRolesAsync(user)).FirstOrDefault();
            }

            return users;
        }

        public async Task<User> GetByIdAsync(Guid id)
        {
            var user = await _userManager.FindByIdAsync(id.ToString()) ?? throw new EntityNotFoundException(typeof(User));

            user.Role = (await _userManager.GetRolesAsync(user)).FirstOrDefault();

            return user;
        }

        public async Task UpdateAsync(User user)
        {
            var identityUser = await _userManager.FindByIdAsync(user.Id.ToString()) ?? throw new EntityNotFoundException(typeof(User));

            identityUser.UserName = user.UserName;
            identityUser.Email = user.Email;
            identityUser.Role = user.Role;

            var result = (await _userManager.UpdateAsync(identityUser));

            if (!result.Succeeded)
            {
                throw new EntityUpdateException(typeof(User), result.Errors.ToString());
            }
        }

        public async Task DeleteAsync(Guid id)
        {
            var result = await _userManager.DeleteAsync(await _userManager.FindByIdAsync(id.ToString()));

            if (!result.Succeeded)
            {
                throw new EntityDeleteException(typeof(User), result.Errors.ToString());
            }
        }
    }
}
