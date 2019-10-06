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
    public class DictionaryController : Controller
    {
        private readonly IDictionaryService _service;
        private readonly IMapper _mapper;

        public DictionaryController(IDictionaryService service,
                                    IMapper mapper)
        {
            _service = service;
            _mapper = mapper;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<DictionaryDto>>> GetAsync()
        {
            return _mapper.Map<List<DictionaryDto>>(await _service.GetAsync());
        }

        [HttpGet("{id}", Name = "GetDictionaryById")]
        public async Task<ActionResult<DictionaryDto>> GetDictionaryByIdAsync(Guid id)
        {
            return _mapper.Map<DictionaryDto>(await _service.GetByIdAsync(id));
        }

        [HttpPost]
        public async Task<ActionResult> InsertAsync([FromBody] DictionaryDto dictionary)
        {
            var inserted = await _service.InsertAsync(_mapper.Map<Dictionary>(dictionary));

            return CreatedAtAction("GetDictionaryById", new { id = inserted.Id }, inserted); ;
        }

        [HttpPut]
        public async Task<ActionResult> UpdateAsync([FromBody] DictionaryDto dictionary)
        {
            await _service.UpdateAsync(_mapper.Map<Dictionary>(dictionary));

            return Ok();
        }

        [HttpDelete("{id}")]
        public async Task<ActionResult> DeleteAsync(Guid id)
        {
            await _service.DeleteAsync(id);

            return Ok();
        }

        [HttpGet("user/{id}")]
        public async Task<ActionResult<IEnumerable<DictionaryDto>>> GetByUserIdAsync(Guid id)
        {
            return _mapper.Map<List<DictionaryDto>>(await _service.GetByUserIdAsync(id));
        }

        [HttpGet("public/{id}")]
        public async Task<ActionResult<IEnumerable<DictionaryDto>>> GetPublic(Guid id)
        {
            return _mapper.Map<List<DictionaryDto>>(await _service.GetPublicAsync(id));
        }
    }
}
