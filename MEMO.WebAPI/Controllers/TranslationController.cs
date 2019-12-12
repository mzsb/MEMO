using AutoMapper;
using MEMO.BLL.Interfaces;
using MEMO.DAL.Entities;
using MEMO.DTO;
using MEMO.DTO.Enums;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace MEMO.WebAPI.Controllers
{
    [Route("api/[Controller]")]
    public class TranslationController : ControllerBase
    {
        private readonly ITranslationService _service;
        private readonly IMapper _mapper;

        public TranslationController(ITranslationService service,
                                     IMapper mapper)
        {
            _service = service;
            _mapper = mapper;
        }

        [Authorize(Roles = "Administrator")]
        [HttpGet]
        public async Task<ActionResult<IEnumerable<TranslationDto>>> GetAsync()
        {
            return _mapper.Map<List<TranslationDto>>(await _service.GetAsync());
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpGet("{id}", Name = "GetTranslationById")]
        public async Task<ActionResult<TranslationDto>> GetTranslationByIdAsync(Guid id)
        {
            return _mapper.Map<TranslationDto>(await _service.GetByIdAsync(id));
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpPost]
        public async Task<ActionResult> InsertAsync([FromBody] TranslationDto translation)
        {
            _service.Token = await getAccessToken();

            var inserted = await _service.InsertAsync(_mapper.Map<Translation>(translation));

            return CreatedAtAction("GetTranslationById", new { id = inserted.Id }, inserted);
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpPut]
        public async Task<ActionResult> UpdateAsync([FromBody] TranslationDto translation)
        {
            _service.Token = await getAccessToken();

            await _service.UpdateAsync(_mapper.Map<Translation>(translation));

            return Ok();
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpDelete("{id}")]
        public async Task<ActionResult> DeleteAsync(Guid id)
        {
            _service.Token = await getAccessToken();

            await _service.DeleteAsync(id);

            return Ok();
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpGet("dictionary/{id}")]
        public async Task<ActionResult<IEnumerable<TranslationDto>>> GetByDictionaryIdAsync(Guid id)
        {
            return _mapper.Map<List<TranslationDto>>(await _service.GetByDictionaryIdAsync(id));
        }

        [HttpGet("translate/{original}/{from}/{to}")]
        public async Task<Translation> TranslateAsync(string original, string from, string to)
        {
            return await _service.TranslateAsync(original, from, to);
        }

        private async Task<string> getAccessToken() => await HttpContext.GetTokenAsync("access_token");
    }
}
