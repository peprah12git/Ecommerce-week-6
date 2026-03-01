import { GraphQLClient, gql } from 'graphql-request';

const GRAPHQL_URL = process.env.REACT_APP_GRAPHQL_URL || 'http://localhost:8080/graphql';

const client = new GraphQLClient(GRAPHQL_URL);

const graphqlService = {
  // Fetch all products with pagination
  getAllProducts: async (page = 0, size = 10) => {
    const query = gql`
      query GetProductsPaged($page: Int, $size: Int) {
        productsPaged(page: $page, size: $size) {
          content {
            productId
            productName
            description
            price
            categoryId
            categoryName
            createdAt
            inventory {
              quantityAvailable
            }
          }
          page
          size
          totalElements
          totalPages
        }
      }
    `;
    const data = await client.request(query, { page, size });
    return data.productsPaged;
  },

  // Fetch products with filters and pagination
  getProducts: async (params = {}) => {
    const { category, minPrice, maxPrice, searchTerm, page = 0, size = 10 } = params;
    
    const query = gql`
      query GetProductsPaged($category: String, $minPrice: Float, $maxPrice: Float, $searchTerm: String, $page: Int, $size: Int) {
        productsPaged(
          category: $category
          minPrice: $minPrice
          maxPrice: $maxPrice
          searchTerm: $searchTerm
          page: $page
          size: $size
        ) {
          content {
            productId
            productName
            description
            price
            categoryId
            categoryName
            createdAt
            inventory {
              quantityAvailable
            }
          }
          page
          size
          totalElements
          totalPages
        }
      }
    `;
    
    const variables = {
      category: category || null,
      minPrice: minPrice ? parseFloat(minPrice) : null,
      maxPrice: maxPrice ? parseFloat(maxPrice) : null,
      searchTerm: searchTerm || null,
      page,
      size,
    };
    
    const data = await client.request(query, variables);
    return data.productsPaged;
  },

  // Fetch single product by ID
  getProductById: async (id) => {
    const query = gql`
      query GetProduct($id: Int!) {
        product(id: $id) {
          productId
          productName
          description
          price
          categoryId
          categoryName
          createdAt
        }
      }
    `;
    const data = await client.request(query, { id: parseInt(id) });
    return data.product;
  },

  // Fetch all categories
  getAllCategories: async () => {
    const query = gql`
      query {
        categories {
          categoryId
          categoryName
          description
          createdAt
        }
      }
    `;
    const data = await client.request(query);
    return data.categories;
  },

  // Fetch single category by ID
  getCategoryById: async (id) => {
    const query = gql`
      query GetCategory($id: Int!) {
        category(id: $id) {
          categoryId
          categoryName
          description
          createdAt
        }
      }
    `;
    const data = await client.request(query, { id: parseInt(id) });
    return data.category;
  },
};

export default graphqlService;
