using MEMO.BLL.Authentication;
using MEMO.BLL.Authorization;
using MEMO.BLL.Exceptions;
using MEMO.BLL.Interfaces;
using MEMO.DAL.Context;
using MEMO.DAL.Entities;
using MEMO.DAL.Enums;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MEMO.BLL.Services
{
    public class UserService : IUserService
    {
        public string Token { private get; set; }

        private readonly MEMOContext _context;
        private readonly UserManager<User> _userManager;
        public readonly AuthorizationManager _authorizationManager;

        public UserService(MEMOContext context,
                           UserManager<User> userManager,
                           AuthorizationManager authorizationManager)
        {
            _context = context;
            _userManager = userManager;
            _authorizationManager = authorizationManager;
        }

        public async Task<IEnumerable<User>> GetAsync()
        {
            var users = await _userManager.Users
                                          .AsNoTracking()
                                          .ToListAsync();

            foreach (var user in users)
            {
                user.Role = (await _userManager.GetRolesAsync(user)).FirstOrDefault();

                foreach (var userDictionary in await _context.UserDictionaries
                                                             .Include(ud => ud.Dictionary)
                                                             .AsNoTracking()
                                                             .Where(ud => ud.UserId == user.Id)
                                                             .ToListAsync())
                {
                    if (userDictionary.Type == UserType.owner)
                    {
                        user.DictionaryCount++;
                        user.TranslationCount += await _context.Translations
                                                               .Where(t => t.Id == userDictionary.DictionaryId)
                                                               .CountAsync();
                    }
                    else
                    {
                        user.ViewedDictionaryCount++;
                    }
                }
            }

            return users;
        }

        public async Task<User> GetByIdAsync(Guid id)
        {
            var user = await _userManager.FindByIdAsync(id.ToString()) ?? throw new EntityNotFoundException(typeof(User));

            user.Role = (await _userManager.GetRolesAsync(user)).FirstOrDefault();

            user.Attributes = await _context.Attributes.Include(a => a.AttributeParameters)
                                                       .Where(a => a.UserId == id)
                                                       .AsNoTracking()
                                                       .ToListAsync();

            foreach (var userDictionary in await _context.UserDictionaries
                                                         .Include(ud => ud.Dictionary)
                                                         .AsNoTracking()
                                                         .Where(ud => ud.UserId == user.Id)
                                                         .ToListAsync())
            {
                if(userDictionary.Type == UserType.owner)
                {
                    user.DictionaryCount++;
                    user.TranslationCount += await _context.Translations
                                                           .Where(t => t.Id == userDictionary.DictionaryId)
                                                           .CountAsync();
                }
                else
                {
                    user.ViewedDictionaryCount++;
                }
            }

            return user;
        }

        public async Task UpdateAsync(User user)
        {
            if (_authorizationManager.authorizeByUserId(user.Id, Token))
            {
                var identityUser = await _userManager.FindByIdAsync(user.Id.ToString()) 
                                                     ?? throw new EntityNotFoundException(typeof(User));

                identityUser.UserName = user.UserName;
                identityUser.Email = user.Email;
                identityUser.Role = user.Role;

                var result = (await _userManager.UpdateAsync(identityUser));

                if (!result.Succeeded)
                {
                    throw new EntityUpdateException("A felhasználónév már foglalt");
                }
            }
            else
            {
                throw new AuthorizationException(typeof(User));
            }
        }

        public async Task DeleteAsync(Guid id)
        {
            if (_authorizationManager.authorizeByUserId(id, Token))
            {
                foreach (var attribute in _context.Attributes.Where(a => a.UserId == id))
                {
                    _context.Attributes.Remove(attribute);
                }

                foreach(var dictionary in _context.UserDictionaries.Include(ud => ud.Dictionary)
                                                                   .Where(ud => ud.Type == UserType.owner &&
                                                                          ud.UserId == id)
                                                                   .Select(ud => ud.Dictionary))
                {
                    _context.Dictionaries.Remove(dictionary);
                }

                try 
                {
                    await _context.SaveChangesAsync();
                }
                catch (DbUpdateException e)
                {
                    throw new EntityDeleteException(typeof(User), e.Message);
                }

                var result = await _userManager.DeleteAsync(await _userManager.FindByIdAsync(id.ToString()));

                if (!result.Succeeded)
                {
                    throw new EntityDeleteException(typeof(User), result.Errors.ToString());
                }
            }
            else
            {
                throw new AuthorizationException(typeof(User));
            }
        }

        public async Task<IEnumerable<User>> GetViewersByUserIdAsync(Guid id)
        {
            var dictionaryIds = await _context.UserDictionaries
                                              .Where(ud => ud.Type == UserType.owner &&
                                                           ud.UserId == id)
                                              .AsNoTracking()
                                              .Select(ud => ud.DictionaryId)
                                              .ToListAsync();

            var users = new List<User>();

            foreach(var user in await _context.UserDictionaries
                                              .Include(ud => ud.User)
                                              .Where(ud => ud.Type == UserType.viewer &&
                                                           dictionaryIds.Contains(ud.DictionaryId))
                                              .AsNoTracking()
                                              .Select(ud => ud.User)
                                              .ToListAsync())
            {
                if (!users.Select(u => u.Id).Contains(user.Id))
                {
                    users.Add(user);
                }
            }

            foreach (var user in users)
            {
                user.Role = (await _userManager.GetRolesAsync(user)).FirstOrDefault();

                foreach (var userDictionary in await _context.UserDictionaries
                                                             .Include(ud => ud.Dictionary)
                                                             .AsNoTracking()
                                                             .Where(ud => ud.UserId == user.Id)
                                                             .ToListAsync())
                {
                    if (userDictionary.Type == UserType.owner)
                    {
                        user.DictionaryCount++;
                        user.TranslationCount += await _context.Translations
                                                               .Where(t => t.DictionaryId == userDictionary.DictionaryId)
                                                               .CountAsync();
                    }
                    else
                    {
                        user.ViewedDictionaryCount++;
                    }
                }
            }

            return users;
        }
    }
}
