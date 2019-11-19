using MEMO.BLL.Authentication;
using MEMO.BLL.Exceptions;
using MEMO.BLL.Interfaces;
using MEMO.DAL.Entities;
using Microsoft.AspNetCore.Identity;
using Microsoft.Data.SqlClient;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MEMO.BLL.Services
{
    public class AuthenticationService : IAuthenticationService
    {
        private readonly UserManager<User> _userManager;
        private readonly TokenManager _tokenManager;

        public AuthenticationService(UserManager<User> userManager, TokenManager tokenManager)
        {
            _userManager = userManager;
            _tokenManager = tokenManager;
        }

        public async Task<TokenHolder> LoginAsync(Login login)
        {
            var user = await _userManager.FindByNameAsync(login.UserName);

            if (user != null)
            {
                if (await _userManager.CheckPasswordAsync(user, login.Password))
                {
                    user.Role = (await _userManager.GetRolesAsync(user)).FirstOrDefault();

                    return new TokenHolder { UserId = user.Id, Token = _tokenManager.GenerateToken(user) };
                }
                else
                {
                    throw new LoginFailedException("Helytelen jelszó");
                }
            }
            else
            {
                throw new LoginFailedException("Helytelen felhasználónév");
            }
        }

        public async Task<TokenHolder> AutoLoginAsync(string token)
        {
            User user = await _userManager.FindByIdAsync(_tokenManager.DecodeUserId(token)) ?? throw new EntityNotFoundException(typeof(User));

            return new TokenHolder { UserId = user.Id, Token = token };
        }

        public async Task<TokenHolder> RegistrationAsync(Registration registration)
        {
            var user = new User
            {
                UserName = registration.UserName,
                Email = registration.Email
            };

            try
            {
                var result = await _userManager.CreateAsync(user, registration.Password);

                if (result.Succeeded)
                {
                    await _userManager.AddToRoleAsync(user, "User");

                    user.Role = (await _userManager.GetRolesAsync(user)).FirstOrDefault();

                    return new TokenHolder { UserId = user.Id, Token = _tokenManager.GenerateToken(user) };
                }
                else
                {
                    throw new RegistrationFailedException();
                }
            }
            catch(SqlException e)
            {
                if (e.Message.Contains("duplicate key"))
                {
                    throw new RegistrationFailedException("A felhasználónév már foglalt");
                }
                else 
                {
                    throw e;
                }
            }
        }

        public async Task<TokenHolder> RefreshTokenAsync(string token)
        {
            User user = await _userManager.FindByIdAsync(_tokenManager.DecodeUserId(token));

            user.Role = (await _userManager.GetRolesAsync(user)).FirstOrDefault();

            return new TokenHolder { UserId = user.Id, Token = _tokenManager.GenerateToken(user) };
        }
    }
}
