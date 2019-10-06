using MEMO.BLL.Interfaces;
using MEMO.DAL.Context;
using MEMO.DAL.Entities;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace MEMO.BLL.Services
{
    public class LanguageService : ILanguageService
    {
        private readonly MEMOContext _context;

        public LanguageService(MEMOContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<Language>> GetAsync()
        {
            return await _context.Languages.ToListAsync();
        }

    }
}
