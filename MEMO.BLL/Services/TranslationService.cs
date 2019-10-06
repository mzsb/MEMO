using MEMO.BLL.Exceptions;
using MEMO.BLL.Interfaces;
using MEMO.DAL.Context;
using MEMO.DAL.Entities;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MEMO.BLL.Services
{
    public class TranslationService : ITranslationService
    {
        private readonly MEMOContext _context;

        public TranslationService(MEMOContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<Translation>> GetAsync()
        {
            return await _context.Translations.Include(t => t.TranslationMetas)
                                              .ThenInclude(tm => tm.MetaDefinition)
                                              .AsNoTracking()
                                              .ToListAsync();
        }

        public async Task<Translation> GetByIdAsync(Guid id)
        {
            return await _context.Translations.Where(t => t.Id == id)
                                              .Include(t => t.TranslationMetas)
                                              .ThenInclude(tm => tm.MetaDefinition)
                                              .AsNoTracking()
                                              .SingleOrDefaultAsync()
                                              ?? throw new EntityNotFoundException(typeof(Translation));
        }

        public async Task<Translation> InsertAsync(Translation translation)
        {

            var inserted = _context.Add(translation).Entity;

            try
            {
                await _context.SaveChangesAsync();

                inserted = await GetByIdAsync(inserted.Id);

                return inserted;
            }
            catch (DbUpdateException e)
            {
                throw new EntityInsertException(typeof(Translation), e.Message);
            }
        }

        public async Task UpdateAsync(Translation translation)
        {
            foreach (var tm in _context.TranslationMetas.Where(tm => tm.TranslationId == translation.Id))
            {
                _context.TranslationMetas.Remove(tm);
            }

            _context.Attach(translation).State = EntityState.Modified;

            foreach (var translationMeta in translation.TranslationMetas) {

                foreach (var property in _context.Entry(translationMeta).Properties)
                {
                    if (!(property.CurrentValue is Guid))
                    {
                        property.IsModified = true;
                    }
                }
            }

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateException e)
            {
                throw new EntityUpdateException(typeof(Translation), e.Message);
            }
        }

        public async Task DeleteAsync(Guid id)
        {
            _context.Remove(await _context.Translations
                                          .FindAsync(id)
                                          ?? throw new EntityNotFoundException(typeof(Translation))
            );

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateException e)
            {
                throw new EntityDeleteException(typeof(Translation), e.Message);
            }
        }

        public async Task<IEnumerable<Translation>> GetByDictionaryIdAsync(Guid id)
        {
            return await _context.Translations.Where(t => t.DictionaryId == id)
                                              .Include(t => t.TranslationMetas)
                                              .ThenInclude(tm => tm.MetaDefinition)
                                              .ThenInclude(md => md.MetaDefinitionParameters)
                                              .AsNoTracking()
                                              .ToListAsync();
        }
    }
}
