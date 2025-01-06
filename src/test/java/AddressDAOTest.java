
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.thesquad.connection.DBConnection;
import com.thesquad.dao.AddressDAO;
import com.thesquad.models.AddressModel;

class AddressDAOTest {

    private AddressDAO addressDAO;
    private DBConnection mockConnection;
    private Connection mockDBConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        addressDAO = new AddressDAO();
        mockConnection = mock(DBConnection.class);
        mockDBConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        when(mockConnection.getConnection()).thenReturn(mockDBConnection);
        when(mockDBConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }
    @Test
    void testCreateAddress() throws Exception {
        AddressModel address = new AddressModel();
        address.setStreet("Main Street");
        address.setHouseNumber(123);
        address.setNeighborhood("Downtown");
        address.setDistrictId(1);

        doNothing().when(mockPreparedStatement).executeUpdate();

        addressDAO.create(address, mockConnection);

        verify(mockPreparedStatement, times(1)).setString(1, "Main Street");
        verify(mockPreparedStatement, times(1)).setInt(2, 123);
        verify(mockPreparedStatement, times(1)).setString(3, "Downtown");
        verify(mockPreparedStatement, times(1)).setInt(4, 1);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdateAddress() throws Exception {
        AddressModel address = new AddressModel();
        address.setAddressId(1);
        address.setStreet("Updated Street");
        address.setHouseNumber(456);
        address.setNeighborhood("New Neighborhood");
        address.setDistrictId(2);

        doNothing().when(mockPreparedStatement).executeUpdate();

        addressDAO.update(address, mockConnection);

        verify(mockPreparedStatement, times(1)).setString(1, "Updated Street");
        verify(mockPreparedStatement, times(1)).setInt(2, 456);
        verify(mockPreparedStatement, times(1)).setString(3, "New Neighborhood");
        verify(mockPreparedStatement, times(1)).setInt(4, 2);
        verify(mockPreparedStatement, times(1)).setInt(5, 1);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteAddress() throws Exception {
        int addressId = 1;

        doNothing().when(mockPreparedStatement).executeUpdate();

        addressDAO.delete(addressId, mockConnection);

        verify(mockPreparedStatement, times(1)).setInt(1, addressId);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetAllAddresses() throws Exception {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("address_id")).thenReturn(1, 2);
        when(mockResultSet.getString("street")).thenReturn("Street1", "Street2");
        when(mockResultSet.getInt("house_number")).thenReturn(10, 20);
        when(mockResultSet.getString("neighborhood")).thenReturn("Neighborhood1", "Neighborhood2");
        when(mockResultSet.getInt("district_id")).thenReturn(101, 102);
        when(mockResultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

        List<AddressModel> addresses = addressDAO.getAll(mockConnection);

        assertNotNull(addresses);
        assertEquals(2, addresses.size());
        assertEquals("Street1", addresses.get(0).getStreet());
        assertEquals("Street2", addresses.get(1).getStreet());
    }

    @Test
    void testGetAddressById() throws Exception {
        int addressId = 1;

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("address_id")).thenReturn(1);
        when(mockResultSet.getString("street")).thenReturn("Main Street");
        when(mockResultSet.getInt("house_number")).thenReturn(123);
        when(mockResultSet.getString("neighborhood")).thenReturn("Downtown");
        when(mockResultSet.getInt("district_id")).thenReturn(1);
        when(mockResultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

        AddressModel address = addressDAO.getAddressById(addressId, mockConnection);

        assertNotNull(address);
        assertEquals(1, address.getAddressId());
        assertEquals("Main Street", address.getStreet());
        assertEquals(123, address.getHouseNumber());
        assertEquals("Downtown", address.getNeighborhood());
    }
}
