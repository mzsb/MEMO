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
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace MEMO.BLL.Services
{
    public class DictionaryService : IDictionaryService
    {
        public string Token { private get; set; }

        private readonly MEMOContext _context;
        private readonly UserManager<User> _userManager;
        public readonly AuthorizationManager _authorizationManager;

        public DictionaryService(MEMOContext context,
                                 UserManager<User> userManager,
                                 AuthorizationManager authorizationManager)
        {
            _context = context;
            _userManager = userManager;
            _authorizationManager = authorizationManager;
        }

        public async Task<IEnumerable<Dictionary>> GetAsync()
        {
            var dictionaries = await _context.Dictionaries.Include(d => d.DictionaryLanguages)
                                                          .ThenInclude(dl => dl.Language)
                                                          .Include(d => d.UserDictionaries)
                                                          .ThenInclude(ud => ud.User)
                                                          .AsNoTracking()
                                                          .ToListAsync();

            foreach(var dictionary in dictionaries)
            {
                dictionary.ViewerCount = dictionary.UserDictionaries.Count - 1;
                dictionary.TranslationCount = await _context.Translations
                                                            .Where(t => t.DictionaryId == dictionary.Id)
                                                            .CountAsync();
            }

            return dictionaries;
        }

        public async Task<Dictionary> GetByIdAsync(Guid id)
        {
            var dictionary = await _context.Dictionaries.Include(d => d.UserDictionaries)
                                                        .ThenInclude(ud => ud.User)
                                                        .Include(d => d.DictionaryLanguages)
                                                        .ThenInclude(dl => dl.Language)
                                                        .Include(d => d.Translations)
                                                        .ThenInclude(t => t.AttributeValues)
                                                        .ThenInclude(av => av.Attribute)
                                                        .AsNoTracking()
                                                        .SingleOrDefaultAsync(d => d.Id == id)
                                                        ?? throw new EntityNotFoundException(typeof(Dictionary));


            dictionary.ViewerCount = dictionary.UserDictionaries.Count - 1;
            dictionary.TranslationCount = await _context.Translations
                                                        .Where(t => t.DictionaryId == dictionary.Id)
                                                        .CountAsync();

            return dictionary;
        }

        public async Task<Dictionary> InsertAsync(Dictionary dictionary)
        {
            var userId = dictionary.UserDictionaries.Where(ud => ud.Type == UserType.owner)
                                                    .Select(ud => ud.User.Id)
                                                    .SingleOrDefault();

            if (_authorizationManager.authorizeByUserId(userId, Token))
            {
                foreach (var ud in dictionary.UserDictionaries)
                {
                    ud.User = await _userManager.FindByIdAsync(ud.User.Id.ToString()) ?? throw new EntityNotFoundException(typeof(User));
                }

                var inserted = _context.Add(dictionary).Entity;

                try
                {
                    await _context.SaveChangesAsync();

                    inserted = await GetByIdAsync(inserted.Id);

                    return inserted;
                }
                catch (DbUpdateException e)
                {
                    throw new EntityInsertException(typeof(Dictionary), e.Message);
                }
            }
            else
            {
                throw new AuthorizationException(typeof(Dictionary));
            }
        }

        public async Task UpdateAsync(Dictionary dictionary)
        {
            var userId = await _context.UserDictionaries.Where(ud => ud.Type == UserType.owner &&
                                                               ud.DictionaryId == dictionary.Id)
                                                        .Select(ud => ud.UserId)
                                                        .SingleOrDefaultAsync();

            if (_authorizationManager.authorizeByUserId(userId, Token))
            {
                foreach (var ud in _context.UserDictionaries.Where(tm => tm.DictionaryId == dictionary.Id))
                {
                    _context.UserDictionaries.Remove(ud);
                }

                foreach (var dl in _context.DictionaryLanguages.Where(dl => dl.DictionaryId == dictionary.Id))
                {
                    _context.DictionaryLanguages.Remove(dl);
                }

                if (!dictionary.IsPublic)
                {
                    dictionary.UserDictionaries = dictionary.UserDictionaries
                                                            .Where(ud => ud.Type == UserType.owner)
                                                            .ToList();
                }

                _context.Attach(dictionary).State = EntityState.Modified;

                try
                {
                    await _context.SaveChangesAsync();
                }
                catch (DbUpdateException e)
                {
                    throw new EntityUpdateException(typeof(Dictionary), e.Message);
                }
            }
            else
            {
                throw new AuthorizationException(typeof(Dictionary));
            }
        }

        public async Task DeleteAsync(Guid id)
        {
            var userDictionary = await _context.UserDictionaries.Include(ud => ud.Dictionary)
                                                                .SingleOrDefaultAsync(ud => ud.DictionaryId == id && 
                                                                                            ud.Type == UserType.owner)
                                                                ?? throw new EntityNotFoundException(typeof(Dictionary));


            if (_authorizationManager.authorizeByUserId(userDictionary.UserId, Token))
            {
                _context.Remove(userDictionary.Dictionary);

                try
                {
                    await _context.SaveChangesAsync();
                }
                catch (DbUpdateException e)
                {
                    throw new EntityDeleteException(typeof(Dictionary), e.Message);
                }
            }
            else
            {
                throw new AuthorizationException(typeof(Dictionary));
            }
        }

        public async Task<IEnumerable<Dictionary>> GetByUserIdAsync(Guid id)
        {
            var userDictionaries = await _context.UserDictionaries.Where(ud => ud.UserId == id)
                                                                  .Include(ud => ud.User)
                                                                  .Include(ud => ud.Dictionary)
                                                                  .ThenInclude(d => d.DictionaryLanguages)
                                                                  .ThenInclude(dl => dl.Language)
                                                                  .Include(ud => ud.Dictionary)
                                                                  .AsNoTracking()
                                                                  .ToListAsync();

            var dictionaries = new List<Dictionary>();

            foreach (var userDictionary in userDictionaries)
            {
                var dictionary = userDictionary.Dictionary;

                if (!dictionaries.Select(d => d.Id).Any(id => id == dictionary.Id))
                {
                    if (!dictionary.UserDictionaries.Any(ud => ud.UserId == id && ud.Type == UserType.owner))
                    {
                        var viewerUserDictionary = dictionary.UserDictionaries
                                                             .SingleOrDefault(ud => ud.UserId == id)
                                                             ?? throw new EntityNotFoundException(typeof(UserDictionary));

                        dictionary.UserDictionaries.Clear();
                        dictionary.UserDictionaries.Add(viewerUserDictionary);
                        dictionary.UserDictionaries.Add(
                                await _context.UserDictionaries
                                              .Include(ud => ud.User)
                                              .SingleOrDefaultAsync(ud => ud.DictionaryId == dictionary.Id &&
                                                                          ud.Type == UserType.owner));
                    }
                    else {
                        dictionary.UserDictionaries = await _context.UserDictionaries
                                                                    .Include(ud => ud.User)
                                                                    .Where(ud => ud.DictionaryId == dictionary.Id)
                                                                    .AsNoTracking()
                                                                    .ToListAsync();
                    }

                    dictionary.TranslationCount = await _context.Translations
                                                                .Where(t => t.DictionaryId == dictionary.Id)
                                                                .CountAsync();

                    dictionary.ViewerCount = await _context.UserDictionaries
                                                           .Where(ud => ud.DictionaryId == dictionary.Id)
                                                           .CountAsync() - 1;

                    dictionaries.Add(dictionary);
                }
            }

            return dictionaries;
        }

        public async Task<IEnumerable<Dictionary>> GetPublicAsync(Guid userId)
        {
            var dictionaries = await _context.Dictionaries.Include(d => d.UserDictionaries)
                                                          .ThenInclude(ud => ud.User)
                                                          .Include(d => d.DictionaryLanguages)
                                                          .ThenInclude(dl => dl.Language)
                                                          .Where(d => d.IsPublic)
                                                          .AsNoTracking()
                                                          .ToListAsync();

            foreach (var dictionary in dictionaries)
            {
                dictionary.ViewerCount = dictionary.UserDictionaries.Count - 1;

                if (!dictionary.UserDictionaries.Any(ud => ud.Type == UserType.owner &&
                                                           ud.UserId == userId))
                {
                    dictionary.UserDictionaries = dictionary.UserDictionaries.Where(ud => ud.Type == UserType.owner || ud.UserId == userId)
                                                                             .ToList();
                }

                dictionary.TranslationCount = await _context.Translations
                                                            .Where(t => t.DictionaryId == dictionary.Id)
                                                            .CountAsync();
            }

            return dictionaries;
        }

        public async Task<IEnumerable<Dictionary>> GetFastAccessibleAsync(Guid id)
        {
            if (_authorizationManager.authorizeByUserId(id, Token))
            {
                return await _context.UserDictionaries.Where(ud => ud.Type == UserType.owner &&
                                                               ud.UserId == id &&
                                                               ud.Dictionary.IsFastAccessible)
                                                      .Select(ud => ud.Dictionary)
                                                      .AsNoTracking()
                                                      .ToListAsync();
            }
            else
            {
                throw new AuthorizationException(typeof(Dictionary));
            }
        }

        public async Task SubscribeAsync(Guid userId, Dictionary dictionary)
        {
            var user = await _context.Users.SingleOrDefaultAsync(u => u.Id == userId)
                                           ?? throw new EntityUpdateException(typeof(User));

            if(await _context.Dictionaries.SingleOrDefaultAsync(d => d.Id == dictionary.Id) == null)
            {
                throw new EntityUpdateException(typeof(Dictionary));
            }

            if (_context.UserDictionaries.Where(ud => ud.UserId == userId && 
                                                      ud.DictionaryId == dictionary.Id &&
                                                      ud.Type == UserType.viewer)
                                         .Any())
            {
                throw new EntityUpdateException(typeof(Dictionary));
            }

            if (_authorizationManager.authorizeByUserId(user.Id, Token))
            {
                _context.UserDictionaries.Add(new UserDictionary
                {
                    UserId = userId,
                    DictionaryId = dictionary.Id,
                    Type = UserType.viewer
                });

                try
                {
                    await _context.SaveChangesAsync();
                }
                catch (DbUpdateException e)
                {
                    throw new EntityInsertException(typeof(UserDictionary), e.Message);
                }
            }
            else
            {
                throw new AuthorizationException(typeof(Dictionary));
            }
        }

        public async Task UnsubscribeAsync(Guid userId, Dictionary dictionary)
        {
            var user = await _context.Users.SingleOrDefaultAsync(u => u.Id == userId)
                               ?? throw new EntityUpdateException(typeof(User));

            if (await _context.Dictionaries.SingleOrDefaultAsync(d => d.Id == dictionary.Id) == null)
            {
                throw new EntityInsertException(typeof(UserDictionary),"Az entitás már létezik");
            }

            if (_authorizationManager.authorizeByUserId(user.Id, Token))
            {
                var userDictionary = await _context.UserDictionaries
                                                   .SingleOrDefaultAsync(ud => ud.UserId == user.Id &&
                                                                         ud.DictionaryId == dictionary.Id &&
                                                                         ud.Type == UserType.viewer) 
                                                   ?? throw new EntityUpdateException(typeof(UserDictionary));


                _context.UserDictionaries.Remove(userDictionary);

                try
                {
                    await _context.SaveChangesAsync();
                }
                catch (DbUpdateException e)
                {
                    throw new EntityDeleteException(typeof(UserDictionary), e.Message);
                }
            }
            else
            {
                throw new AuthorizationException(typeof(Dictionary));
            }
        }
    }
}
