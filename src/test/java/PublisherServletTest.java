      import com.google.gson.Gson;
      import com.thesquad.connection.DBConnection;
      import com.thesquad.dao.*;
      import com.thesquad.models.*;
      import com.thesquad.utils.Helpers;

      import javax.servlet.http.HttpServletRequest;
      import javax.servlet.http.HttpServletResponse;

      import org.junit.jupiter.api.BeforeEach;
      import org.junit.jupiter.api.Test;
      import org.mockito.ArgumentCaptor;

      import java.io.PrintWriter;
      import java.io.StringWriter;
      import java.util.ArrayList;
      import java.util.List;

      import static org.junit.jupiter.api.Assertions.assertEquals;
      import static org.mockito.Mockito.*;

      import com.thesquad.controllers.PublisherServlet;

      class PublisherServletTest {

          private PublisherServlet publisherServlet;
          private HttpServletRequest request;
          private HttpServletResponse response;
          private DBConnection connection;

          @BeforeEach
          void setUp() {
              publisherServlet = new PublisherServlet();
              request = mock(HttpServletRequest.class);
              response = mock(HttpServletResponse.class);
              connection = mock(DBConnection.class);
          }

          @Test
          void testDoGet_provinceOperation() throws Exception {
              // Mock request parameters
              when(request.getParameter("operation")).thenReturn("province");
              when(request.getParameter("id")).thenReturn("1");

              // Mock DAO behavior
              ProvinceDAO provinceDao = mock(ProvinceDAO.class);
              List<ProvinceModel> provinces = new ArrayList<>();
              ProvinceModel province = new ProvinceModel();
              province.setProvinceId(1);
              province.setName("Test Province");
              provinces.add(province);

              when(provinceDao.getProvincesByCountryId(1, connection)).thenReturn(provinces);

              // Mock response writer
              StringWriter stringWriter = new StringWriter();
              PrintWriter printWriter = new PrintWriter(stringWriter);
              when(response.getWriter()).thenReturn(printWriter);

              // Call the servlet
              publisherServlet.doGet((javax.servlet.http.HttpServletRequest)request, response);

              // Verify response
              printWriter.flush();
              Gson gson = new Gson();
              assertEquals(gson.toJson(provinces), stringWriter.toString().trim());
          }

          @Test
          void testDoGet_deletePublisher() throws Exception {
              // Mock request parameters
              when(request.getParameter("action")).thenReturn("delete");
              when(request.getParameter("id")).thenReturn("1");

              PublisherDAO publisherDao = mock(PublisherDAO.class);

              // Call the servlet
              publisherServlet.doGet((javax.servlet.http.HttpServletRequest)request, response);

              // Verify delete operation
              verify(publisherDao, times(1)).delete(1, connection);
              verify(response).sendRedirect(anyString());
          }

          @Test
          void testDoPost_createPublisher() throws Exception {
              // Mock request parameters
              when(request.getParameter("name")).thenReturn("Publisher A");
              when(request.getParameter("nif")).thenReturn("123456789");
              when(request.getParameter("fax")).thenReturn("12345");
              when(request.getParameter("street")).thenReturn("Main Street");
              when(request.getParameter("houseNum")).thenReturn("10");
              when(request.getParameter("district")).thenReturn("Downtown");
              when(request.getParameter("phone1")).thenReturn("555123456");
              when(request.getParameter("phone2")).thenReturn("");
              when(request.getParameter("email1")).thenReturn("publisher@example.com");
              when(request.getParameter("email2")).thenReturn("");

              // Mock DAO and helpers
              PublisherDAO publisherDao = mock(PublisherDAO.class);
              AddressDAO addressDao = mock(AddressDAO.class);
              Helpers helpers = mock(Helpers.class);
              when(helpers.getIdOfLastRow(anyString(), eq(connection))).thenReturn(1);

              // Call the servlet
              publisherServlet.doPost((javax.servlet.http.HttpServletRequest)request, response);

              // Verify address creation
              ArgumentCaptor<AddressModel> addressCaptor = ArgumentCaptor.forClass(AddressModel.class);
              verify(addressDao).create(addressCaptor.capture(), eq(connection));
              assertEquals("Main Street", addressCaptor.getValue().getStreet());

              // Verify publisher creation
              ArgumentCaptor<PublisherModel> publisherCaptor = ArgumentCaptor.forClass(PublisherModel.class);
              verify(publisherDao).create(publisherCaptor.capture(), eq(connection));
              assertEquals("Publisher A", publisherCaptor.getValue().getName());

              // Verify redirection
              verify(response).sendRedirect(anyString());
          }

          @Test
          void testDoPost_editPublisher() throws Exception {
              // Mock request parameters for edit mode
              when(request.getParameter("edit")).thenReturn("true");
              when(request.getParameter("publisherId")).thenReturn("1");
              when(request.getParameter("phone1Id")).thenReturn("1");
              when(request.getParameter("email1Id")).thenReturn("1");
              when(request.getParameter("addressId")).thenReturn("1");
              when(request.getParameter("name")).thenReturn("Updated Publisher");
              when(request.getParameter("nif")).thenReturn("987654321");
              when(request.getParameter("fax")).thenReturn("54321");

              // Mock DAOs
              PublisherDAO publisherDao = mock(PublisherDAO.class);
              AddressDAO addressDao = mock(AddressDAO.class);

              // Call the servlet
              publisherServlet.doPost((javax.servlet.http.HttpServletRequest)request, response);

              // Verify address update
              ArgumentCaptor<AddressModel> addressCaptor = ArgumentCaptor.forClass(AddressModel.class);
              verify(addressDao).update(addressCaptor.capture(), eq(connection));
              assertEquals(1, addressCaptor.getValue().getAddressId());

              // Verify publisher update
              ArgumentCaptor<PublisherModel> publisherCaptor = ArgumentCaptor.forClass(PublisherModel.class);
              verify(publisherDao).update(publisherCaptor.capture(), eq(connection));
              assertEquals("Updated Publisher", publisherCaptor.getValue().getName());

              // Verify redirection
              verify(response).sendRedirect(anyString());
          }
      }
