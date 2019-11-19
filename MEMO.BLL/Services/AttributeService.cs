using MEMO.BLL.Authorization;
using MEMO.BLL.Exceptions;
using MEMO.BLL.Interfaces;
using MEMO.DAL.Context;
using MEMO.DAL.Entities;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MEMO.BLL.Services
{
    public class AttributeService : IAttributeService
    {
        public string Token { private get; set; }

        private readonly MEMOContext _context;
        private readonly UserManager<User> _userManager;
        public readonly AuthorizationManager _authorizationManager;

        public AttributeService(MEMOContext context,
                                 UserManager<User> userManager,
                                 AuthorizationManager authorizationManager)
        {
            _context = context;
            _userManager = userManager;
            _authorizationManager = authorizationManager;
        }

        public async Task<IEnumerable<Attribute>> GetAsync()
        {
            var attributes = await _context.Attributes
                                           .Include(a => a.AttributeValues)
                                           .Include(a => a.User)
                                           .Include(a => a.AttributeParameters)
                                           .AsNoTracking()
                                           .ToListAsync();

            foreach(var attribute in attributes)
            {
                attribute.AttributeValuesCount = attribute.AttributeValues.Count;
                attribute.AttributeValues.Clear();
            }

            return attributes;
        }

        public async Task<Attribute> GetByIdAsync(System.Guid id)
        {

            return await _context.Attributes.Where(a => a.Id == id)
                                            .Include(a => a.AttributeParameters)
                                            .Include(a => a.AttributeValues)
                                            .AsNoTracking()
                                            .SingleOrDefaultAsync()
                                            ?? throw new EntityNotFoundException(typeof(Attribute));
        }

        public async Task<Attribute> InsertAsync(Attribute attribute)
        {
            if (_authorizationManager.authorizeByUserId(attribute.UserId, Token))
            {
                attribute.User = await _userManager.FindByIdAsync(attribute.UserId.ToString()) ?? throw new EntityNotFoundException(typeof(User));

                var inserted = _context.Add(attribute).Entity;

                try
                {
                    await _context.SaveChangesAsync();

                    inserted = await GetByIdAsync(inserted.Id);

                    return inserted;
                }
                catch (DbUpdateException e)
                {
                    throw new EntityInsertException(typeof(Attribute), e.Message);
                }
            }
            else
            {
                throw new AuthorizationException(typeof(Attribute));
            }
        }

        public async Task UpdateAsync(Attribute attribute)
        {
            var userId = (await _context.Attributes.SingleOrDefaultAsync(a => a.Id == attribute.Id)).UserId;

            if (_authorizationManager.authorizeByUserId(userId, Token))
            {
                foreach (var ap in _context.AttributeParameters.Where(ap => ap.AttributeId == attribute.Id))
                {
                    _context.AttributeParameters.Remove(ap);
                }

                _context.Attach(attribute).State = EntityState.Modified;

                foreach (var attributeParameter in attribute.AttributeParameters)
                {
                    foreach (var property in _context.Entry(attributeParameter).Properties)
                    {
                        if (!(property.CurrentValue is System.Guid))
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
                    throw new EntityUpdateException(typeof(Attribute), e.Message);
                }
            }
            else
            {
                throw new AuthorizationException(typeof(Attribute));
            }
        }

        public async Task DeleteAsync(System.Guid id)
        {
            var attribute = (await _context.Attributes.SingleOrDefaultAsync(a => a.Id == id));

            if (_authorizationManager.authorizeByUserId(attribute.UserId, Token))
            {
                _context.Remove(attribute);

                try
                {
                    await _context.SaveChangesAsync();
                }
                catch (DbUpdateException e)
                {
                    throw new EntityDeleteException(typeof(Attribute), e.Message);
                }
            }
            else
            {
                throw new AuthorizationException(typeof(Attribute));
            }
        }

        public async Task<IEnumerable<Attribute>> GetByUserIdAsync(System.Guid id)
        {
            var attributes = await _context.Attributes.Where(a => a.UserId == id)
                                                      .Include(a => a.AttributeValues)
                                                      .Include(a => a.User)
                                                      .Include(a => a.AttributeParameters)
                                                      .AsNoTracking()
                                                      .ToListAsync();
            foreach (var attribute in attributes)
            {
                attribute.AttributeValuesCount = attribute.AttributeValues.Count;
                attribute.AttributeValues.Clear();
            }

            return attributes;
        }
    }
}
