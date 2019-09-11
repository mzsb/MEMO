using MEMO.BLL.Authentication;
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

        public UserService(MEMOContext context, 
                           UserManager<User> userManager)
        {
            _userManager = userManager;
        }

        public async Task<User> LoginAsync(User user)
        {
            var appUser = await _userManager.FindByNameAsync(user.UserName);

            if (appUser != null && await _userManager.CheckPasswordAsync(appUser, user.Password))
            {
                appUser.Role = (await _userManager.GetRolesAsync(appUser)).FirstOrDefault();

                appUser.Token = TokenManager.GenerateToken(appUser);

                return appUser;
            }
            else
            {
                //TODO: Sikertelen belepes
                return null;
            }
        }

        public async Task<User> AddAsync(User user)
        {
            var result = await _userManager.CreateAsync(user, user.Password);

            if (result.Succeeded)
            {
                await _userManager.AddToRoleAsync(user, "User");

                user.Role = (await _userManager.GetRolesAsync(user)).FirstOrDefault();

                user.Token = TokenManager.GenerateToken(user);

                return user;
            }
            else
            {
                //TODO: Sikertelen regisztracio
                return null;
            }
        }

        public async Task<IEnumerable<User>> GetAsync()
        {
            return await _userManager.Users
                                     .AsNoTracking()
                                     .ToListAsync();
        }

        public async Task<User> GetByIdAsync(Guid id)
        {
            return await _userManager.FindByIdAsync(id.ToString());
        }

        public async Task UpdateAsync(User user)
        {
            await _userManager.UpdateAsync(user);
        }

        public async Task DeleteAsync(User user)
        {
            await _userManager.DeleteAsync(user);
        }
    }
}
