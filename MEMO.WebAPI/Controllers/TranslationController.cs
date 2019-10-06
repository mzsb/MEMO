using AutoMapper;
using MEMO.BLL.Interfaces;
using MEMO.DAL.Entities;
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
    public class TranslationController : Controller
    {
        private readonly ITranslationService _service;
        private readonly IMapper _mapper;

        public TranslationController(ITranslationService service,
                                    IMapper mapper)
        {
            _service = service;
            _mapper = mapper;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<TranslationDto>>> GetAsync()
        {
            return _mapper.Map<List<TranslationDto>>(await _service.GetAsync());
        }

        [HttpGet("{id}", Name = "GetTranslationById")]
        public async Task<ActionResult<TranslationDto>> GetTranslationByIdAsync(Guid id)
        {
            return _mapper.Map<TranslationDto>(await _service.GetByIdAsync(id));
        }

        [HttpPost]
        public async Task<ActionResult> InsertAsync([FromBody] TranslationDto translation)
        {
            var inserted = await _service.InsertAsync(_mapper.Map<Translation>(translation));

            return CreatedAtAction("GetTranslationById", new { id = inserted.Id }, inserted);
        }

        [HttpPut]
        public async Task<ActionResult> UpdateAsync([FromBody] TranslationDto translation)
        {
            await _service.UpdateAsync(_mapper.Map<Translation>(translation));

            return Ok();
        }

        [HttpDelete("{id}")]
        public async Task<ActionResult> DeleteAsync(Guid id)
        {
            await _service.DeleteAsync(id);

            return Ok();
        }

        [HttpGet("dictionary/{id}")]
        public async Task<ActionResult<IEnumerable<TranslationDto>>> GetByDictionaryIdAsync(Guid id)
        {
            return _mapper.Map<List<TranslationDto>>(await _service.GetByDictionaryIdAsync(id));
        }
    }
}
