package reisrijder.reisrijder;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class ReisRijderApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testStationNearbyEndpoint() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/station/nearby"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		mockMvc.perform(MockMvcRequestBuilders.get("/api/station/nearby?lat=52.3459744&lon=4.967104&limit=1"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("[{\"name\":\"Diemen\",\"distance\":149.28047536074018,\"latitude\":52.344897,\"longitude\":4.968411,\"ovBike\":true,\"ovEbike\":false,\"baggage\":false,\"distanceMeters\":149}]"));
	}

	@Test
	public void testAddressAutocompleteEndpoint() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/address/autocomplete")
						.content("{}")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		mockMvc.perform(MockMvcRequestBuilders.post("/api/address/autocomplete")
						.content("{\"address\":\"fizeaustraat amsterdam\"}")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("[{\"name\":\"Fizeaustraat, Amsterdam\",\"latitude\":52.3453524436111,\"longitude\":4.92553048028701}]"));
	}

	@Test
	public void testAddressLocationEndpoint() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/address/location")
						.content("{}")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

}
