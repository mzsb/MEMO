using MEMO.BLL.Authorization;
using MEMO.BLL.Exceptions;
using MEMO.BLL.Interfaces;
using MEMO.DAL.Context;
using MEMO.DAL.Entities;
using MEMO.DAL.Enums;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Web;

namespace MEMO.BLL.Services
{
    public class TranslationService : ITranslationService
    {
        public string Token { private get; set; }

        private readonly MEMOContext _context;
        private readonly AuthorizationManager _authorizationManager;
        private WebClient _webClient = new WebClient { Encoding = System.Text.Encoding.UTF8 };

        public TranslationService(MEMOContext context,
                                  AuthorizationManager authorizationManager)
        {
            _context = context;
            _authorizationManager = authorizationManager;
        }

        public async Task<IEnumerable<Translation>> GetAsync()
        {
            var translations =  await _context.Translations.Include(t => t.AttributeValues)
                                                           .ThenInclude(av => av.Attribute)
                                                           .AsNoTracking()
                                                           .ToListAsync();

            foreach(var translation in translations)
            {
                translation.AttributeValueCount = await _context.AttributeValues
                                                                .Where(av => av.TranslationId == translation.Id)
                                                                .CountAsync();
            }

            return translations;
        }

        public async Task<Translation> GetByIdAsync(Guid id)
        {
            return await _context.Translations.Where(t => t.Id == id)
                                              .Include(t => t.AttributeValues)
                                              .ThenInclude(av => av.Attribute)
                                              .AsNoTracking()
                                              .SingleOrDefaultAsync()
                                              ?? throw new EntityNotFoundException(typeof(Translation));
        }

        public async Task<Translation> InsertAsync(Translation translation)
        {
            var userId = await _context.UserDictionaries.Where(ud => ud.Type == UserType.owner &&
                                                               ud.DictionaryId == translation.DictionaryId)
                                                        .Select(ud => ud.UserId)
                                                        .SingleOrDefaultAsync();

            if (_authorizationManager.AuthorizeByUserId(userId, Token))
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
            else
            {
                throw new AuthorizationException(typeof(Translation));
            }
        }

        public async Task UpdateAsync(Translation translation)
        {
            var userId = await _context.UserDictionaries.Where(ud => ud.Type == UserType.owner &&
                                                               ud.DictionaryId == translation.DictionaryId)
                                                        .Select(ud => ud.UserId)
                                                        .SingleOrDefaultAsync();

            if (_authorizationManager.AuthorizeByUserId(userId, Token))
            {
                foreach (var av in _context.AttributeValues.Where(av => av.TranslationId == translation.Id))
                {
                    _context.AttributeValues.Remove(av);
                }

                _context.Attach(translation).State = EntityState.Modified;

                foreach (var attributeValues in translation.AttributeValues) {

                    foreach (var property in _context.Entry(attributeValues).Properties)
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
            else
            {
                throw new AuthorizationException(typeof(Translation));
            }
        }

        public async Task DeleteAsync(Guid id)
        {
            var translation = await _context.Translations
                                            .FindAsync(id)
                                            ?? throw new EntityNotFoundException(typeof(Translation));

            var userId = await _context.UserDictionaries.Where(ud => ud.Type == UserType.owner &&
                                                               ud.DictionaryId == translation.DictionaryId)
                                                        .Select(ud => ud.UserId)
                                                        .SingleOrDefaultAsync();

            if (_authorizationManager.AuthorizeByUserId(userId, Token))
            {
                _context.Remove(translation);

                try
                {
                    await _context.SaveChangesAsync();
                }
                catch (DbUpdateException e)
                {
                    throw new EntityDeleteException(typeof(Translation), e.Message);
                }
            }
            else
            {
                throw new AuthorizationException(typeof(Translation));
            }
        }

        public async Task<IEnumerable<Translation>> GetByDictionaryIdAsync(Guid id)
        {
            var translations = await _context.Translations
                                             .Where(t => t.DictionaryId == id)
                                             .Include(t => t.AttributeValues)
                                             .ThenInclude(av => av.Attribute)
                                             .ThenInclude(md => md.AttributeParameters)
                                             .AsNoTracking()
                                             .ToListAsync();

            foreach (var translation in translations)
            {
                translation.AttributeValueCount = await _context.AttributeValues
                                                                .Where(av => av.TranslationId == translation.Id)
                                                                .CountAsync();
            }

            return translations;
        }

        public async Task<Translation> TranslateAsync(string original, string from, string to)
        {
            string url = $"https://translate.googleapis.com/translate_a/single?client=gtx&sl={from.ToLower()}&tl={to.ToLower()}&dt=t&q={HttpUtility.UrlEncode(original)}";

            try
            {
                var result = await _webClient.DownloadStringTaskAsync(url);
                return new Translation { Original = original, Translated = result.Substring(4, result.IndexOf("\"", 4, StringComparison.Ordinal) - 4) };
            }
            catch
            {
                throw new TranslationException();
            }
        }
    }
}
