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
                                           .Include(a => a.User)
                                           .Include(a => a.AttributeParameters)
                                           .AsNoTracking()
                                           .ToListAsync();

            foreach(var attribute in attributes)
            {
                attribute.AttributeValuesCount = await _context.AttributeValues
                                                               .Where(av => av.AttributeId == attribute.Id)
                                                               .CountAsync();
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
            if (_authorizationManager.AuthorizeByUserId(attribute.User.Id, Token))
            {
                attribute.User = await _userManager.FindByIdAsync(attribute.User.Id.ToString()) ?? throw new EntityNotFoundException(typeof(User));

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
            var user = await _userManager.FindByIdAsync(attribute.User.Id.ToString()) ?? throw new EntityNotFoundException(typeof(User));

            if (_authorizationManager.AuthorizeByUserId(user.Id, Token))
            {
                attribute.User = user;

                if (attribute.Type == DAL.Enums.AttributeType.spinner)
                {
                    foreach (var attributeValue in _context.AttributeValues
                                                           .Include(av => av.Attribute)
                                                           .ThenInclude(a => a.AttributeParameters)
                                                           .Where(av => av.AttributeId == attribute.Id)
                                                           .AsNoTracking())
                    {
                        foreach (var ap in attributeValue.Attribute.AttributeParameters)
                        {
                            if (attributeValue.Value == ap.Value)
                            {
                                if (attribute.AttributeParameters.Select(nap => nap.Id).Contains(ap.Id))
                                {
                                    attributeValue.Value = attribute.AttributeParameters.SingleOrDefault(nap => nap.Id == ap.Id).Value;
                                    _context.Entry(attributeValue).State = EntityState.Modified;
                                }
                                else{
                                    attributeValue.Attribute = null;
                                    _context.Remove(attributeValue);
                                }
                            }
                        }
                    }
                }

                foreach (var ap in _context.AttributeParameters.Where(ap => ap.AttributeId == attribute.Id))
                { 
                    _context.AttributeParameters.Remove(ap);
                }

                foreach(var ap in attribute.AttributeParameters)
                {
                    _context.Add(ap);
                }
  
                _context.Entry(attribute).State = EntityState.Modified;

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

            if (_authorizationManager.AuthorizeByUserId(attribute.UserId, Token))
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
                                                      .Include(a => a.User)
                                                      .Include(a => a.AttributeParameters)
                                                      .AsNoTracking()
                                                      .ToListAsync();
            foreach (var attribute in attributes)
            {
                attribute.AttributeValuesCount = await _context.AttributeValues
                                                               .Where(av => av.AttributeId == attribute.Id)
                                                               .CountAsync();
            }

            return attributes;
        }
    }
}
