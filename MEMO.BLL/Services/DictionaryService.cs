using MEMO.BLL.Exceptions;
using MEMO.BLL.Interfaces;
using MEMO.DAL.Context;
using MEMO.DAL.Entities;
using MEMO.DTO.Enums;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MEMO.BLL.Services
{
    public class DictionaryService : IDictionaryService {

        private readonly MEMOContext _context;
        private readonly UserManager<User> _userManager;

        public DictionaryService(MEMOContext context,
                                 UserManager<User> userManager)
        {
            _context = context;
            _userManager = userManager;
        }

        public async Task<IEnumerable<Dictionary>> GetAsync()
        {
            return await _context.Dictionaries.Include(d => d.DictionaryLanguages)
                                              .ThenInclude(dl => dl.Language)
                                              .Include(d => d.MetaDefinitions)
                                              .ThenInclude(md => md.MetaDefinitionParameters)
                                              .Include(d => d.UserDictionaries)
                                              .ThenInclude(ud => ud.User)
                                              .AsNoTracking()
                                              .ToListAsync();
        }

        public async Task<Dictionary> GetByIdAsync(Guid id)
        {
            return await _context.Dictionaries.Where(d => d.Id == id)
                                              .Include(d => d.DictionaryLanguages)
                                              .ThenInclude(dl => dl.Language)
                                              .Include(d => d.MetaDefinitions)
                                              .ThenInclude(md => md.MetaDefinitionParameters)
                                              .Include(d => d.UserDictionaries)
                                              .ThenInclude(ud => ud.User)
                                              .AsNoTracking()
                                              .SingleOrDefaultAsync()
                                              ?? throw new EntityNotFoundException(typeof(Dictionary));
        }

        public async Task<Dictionary> InsertAsync(Dictionary dictionary)
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

        public async Task UpdateAsync(Dictionary dictionary)
        {
            foreach (var ud in _context.UserDictionaries.Where(tm => tm.DictionaryId == dictionary.Id))
            {
                _context.UserDictionaries.Remove(ud);
            }

            foreach (var dl in _context.DictionaryLanguages.Where(dl => dl.DictionaryId == dictionary.Id))
            {
                _context.DictionaryLanguages.Remove(dl);
            }

            foreach (var md in _context.MetaDefinitions.Include(md => md.MetaDefinitionParameters)
                                                       .Where(tm => tm.DictionaryId == dictionary.Id))
            {
                _context.MetaDefinitions.Remove(md);
            }

            _context.Attach(dictionary).State = EntityState.Modified;

            foreach (var metaDefinition in dictionary.MetaDefinitions)
            {
                foreach (var property in _context.Entry(metaDefinition).Properties)
                {
                    if (!(property.CurrentValue is Guid))
                    {
                        property.IsModified = true;
                    }
                }

                foreach (var metaDefinitionParameter in metaDefinition.MetaDefinitionParameters)
                {
                    foreach (var property in _context.Entry(metaDefinitionParameter).Properties)
                    {
                        if (!(property.CurrentValue is Guid))
                        {
                            property.IsModified = true;
                        }
                    }
                }
            }

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateException e)
            {
                throw new EntityUpdateException(typeof(Dictionary), e.Message);
            }
        }

        public async Task DeleteAsync(Guid id)
        {
            _context.Remove(await _context.Dictionaries
                                          .FindAsync(id)
                                          ?? throw new EntityNotFoundException(typeof(Dictionary))
            );

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateException e)
            {
                throw new EntityDeleteException(typeof(Dictionary), e.Message);
            }
        }

        public async Task<IEnumerable<Dictionary>> GetByUserIdAsync(Guid id)
        {
            return await _context.UserDictionaries.Where(ud => ud.UserId == id)
                                                  .Include(ud => ud.Dictionary)
                                                  .ThenInclude(d => d.DictionaryLanguages)
                                                  .ThenInclude(dl => dl.Language)
                                                  .Select(ud => ud.Dictionary)
                                                  .AsNoTracking()
                                                  .ToListAsync();
        }

        public async Task<IEnumerable<Dictionary>> GetPublicAsync(Guid id)
        {
            return await _context.UserDictionaries.Where(ud => ud.Type == UserType.owner &&
                                                               ud.UserId != id && 
                                                               ud.Dictionary.IsPublic && 
                                                               ud.Dictionary.Translations.Any())
                                                  .Include(ud => ud.Dictionary)
                                                  .ThenInclude(ud => ud.DictionaryLanguages)
                                                  .ThenInclude(dl => dl.Language)
                                                  .Select(ud => ud.Dictionary)
                                                  .AsNoTracking()
                                                  .ToListAsync();
        }
    }
}
