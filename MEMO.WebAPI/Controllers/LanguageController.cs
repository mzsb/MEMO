using AutoMapper;
using MEMO.BLL.Interfaces;
using MEMO.DTO;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MEMO.WebAPI.Controllers
{
    [Authorize(Roles = "Administator,User")]
    [Route("api/[Controller]")]
    public class LanguageController : Controller
    {
        private readonly ILanguageService _service;
        private readonly IMapper _mapper;

        public LanguageController(ILanguageService service,
                                  IMapper mapper)
        {
            _service = service;
            _mapper = mapper;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<LanguageDto>>> GetAsync()
        {
            return _mapper.Map<List<LanguageDto>>(await _service.GetAsync());
        }
    }
}
